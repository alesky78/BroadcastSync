package it.spaghettisource.broadcastsync.infrastructure;

import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.spaghettisource.broadcastsync.BroadCastSyncConfig;
import it.spaghettisource.broadcastsync.exception.BroadCastSyncExceptionDataProtocolNotRespected;
import it.spaghettisource.broadcastsync.exception.BroadCastSyncExceptionDeserializeData;
import it.spaghettisource.broadcastsync.exception.BroadCastSyncRuntimeException;
import it.spaghettisource.broadcastsync.exception.ExceptionFactory;
import it.spaghettisource.broadcastsync.handler.MessageHandler;
import it.spaghettisource.broadcastsync.message.HeartBeat;
import it.spaghettisource.broadcastsync.message.HeartBeatFactory;
import it.spaghettisource.broadcastsync.message.MessageByteArray;
import it.spaghettisource.broadcastsync.message.MessageObject;
import it.spaghettisource.broadcastsync.message.MessageString;
import it.spaghettisource.broadcastsync.message.MessageType;
import it.spaghettisource.broadcastsync.serializer.ObjectSerializer;
import it.spaghettisource.broadcastsync.serializer.StringSeralizer;

/**
 * the role of the DatagramSequentializer is to reorganize the raw data received and propose them to the {@link MessageHandler}.
 * 
 * BroadCastSyn is able to subdivide a message sent in several chunk then this class has the responsibility to re sequentialize the data and compose the original message before to give them to the {@link}  
 * 
 * @author Alessandro D'Ottavio
 * @version 1.0
 *
 */
public class DatagramSequentializer implements Runnable{

	private static Logger  log = LoggerFactory.getLogger(DatagramSequentializer.class);

	private boolean stopped = true;
	private Thread thread;
	
	private long lastCleaningLoopTime;
	private Map<String,Payload> payloads;
	
	private BroadCastSyncConfig config;
	private ExceptionFactory exceptionFactory;
	private DatagramPacketQueue queue;
	private MessageHandler messageHandler;
	private DatagramPacketDataProtocol protocol;
	
	private HeartBeatFactory heartBeatFactory;
	
	private StringSeralizer stringDeseralizer;
	private ObjectSerializer<Serializable> objectDeseralizer;
	
	public DatagramSequentializer(BroadCastSyncConfig config,ExceptionFactory exceptionFactory, DatagramPacketQueue queue, HeartBeatFactory heartBeatFactory, MessageHandler messageProcessor){
		this.config = config;
		this.exceptionFactory = exceptionFactory;
		this.queue = queue;
		this.messageHandler = messageProcessor;
		this.heartBeatFactory = heartBeatFactory;
		
		protocol = new DatagramPacketDataProtocol(exceptionFactory);
		payloads = new HashMap<String, Payload>();
		
		stringDeseralizer = new StringSeralizer(exceptionFactory);
		objectDeseralizer = new ObjectSerializer<Serializable>(exceptionFactory);
	}
	
	
	/**
	 * The run method represents the core of the DatagramSequentializer thread.
	 * Within the loop, the thread retrieves the most recent datagram from the queue of objects queue and passes it to the process method for processing. 
	 * Subsequently, it is checked whether it is time to delete expired messages. 
	 * 
	 * @param datagram
	 */
	@Override
	public void run() {

		log.info("DatagramSequentializer thread started");
		
		lastCleaningLoopTime = System.currentTimeMillis();
		
		while (!stopped) {
			try {
				
				//get the last datagram from the queue
				DatagramPacket datagram = queue.pop();
				
				process(datagram);
				
				//clean if is the moment
				if((System.currentTimeMillis()-lastCleaningLoopTime)> config.getCleaningExpiredMessageIntervalTimeMillis()) {
					cleanExpiredMessage();
					lastCleaningLoopTime = System.currentTimeMillis();
				}
				
			}catch (InterruptedException e) {
				log.info("DatagramSequentializer interrupted");
                break;
                
            }catch (Exception cause) {
            	BroadCastSyncRuntimeException ex = exceptionFactory.getUnexpectedException(cause);
				log.error(ex.getLocalizedMessage(),ex);
				
			}
		}
		
	}

	/**
	 * The process() method receives a DatagramPacket containing a message and handles the analysis of the message, 
	 * checks whether it's a new message or a fragment of an existing one, and in the latter case, adds the fragment to the correct message.
	 * 
	 * Initially, the datagram is analyzed with the data protocol defined in the {@link DatagramPacketDataProtocol} class. 
	 * Then, the message ID is retrieved from the protocol and searched in the payloads object map. 
	 * If the message ID doesn't exist in the map, then a new Payload object is created for the new message.
	 * 
	 * Next, the current message fragment is added to the corresponding Payload object. 
	 * Finally, it's checked whether the message has been completed, i.e., if all fragments have been correctly received. 
	 * If affirmative, the message is removed from the payloads object map and the {@link DatagramSequentializer#deserializeDataAndCallMessageHandler(String, Payload)} method is called to process the complete message.
	 * 
	 * If errors occur during the message analysis, the message is discarded. 
	 * If the message had already been partially processed, it's removed from the payloads object map.
	 * 
	 * @param datagram
	 */
	protected void process(DatagramPacket datagram) {
		
		log.debug("process message");
		
		String messageId = null;
		
		try {
			protocol.analyzed(datagram);
			
			messageId = protocol.getMessageId();
			Payload payload = payloads.get(messageId); 
			
			//this is a new message, add it in the queue
			if(payload==null) {
				InetAddress address = datagram.getAddress();
				payload = new Payload(address.getHostAddress(), address.getCanonicalHostName(),protocol.getMessageType(), protocol.getTotalPackets());
				
				payloads.put(messageId, payload);
			}
			
			//add the chunk
			PayloadChunk chunk = protocol.buildPayloadChunk();
			payload.addChunk(chunk);
			
			//verify if the payload is completed
			if(payload.isCompleted()) {
				payloads.remove(messageId);
				
				deserializeDataAndCallMessageHandler(messageId, payload);
				
			}

		}catch (BroadCastSyncExceptionDataProtocolNotRespected cause) {
			log.error(cause.getLocalizedMessage(),cause);
			
		}catch (Exception e) {
			//if there is an error processing this message discard the payload, now is impossible rebuild the message
			log.error("unexpected error processing this datagram, payload discarded",e);
			if(messageId!=null) {
				payloads.remove(messageId);
			}
			
		}

	}

	/**
	 * deserialize the message data based on the message type of the payload
	 * and build the correct message to call the proper method of the {@link MessageHandler}
	 * 
	 * @param messageId
	 * @param payload
	 */
	private void deserializeDataAndCallMessageHandler(String messageId, Payload payload) {
		
		log.debug("message "+messageId+" complete, total packets:"+payload.getTotalPackets());
		
		if(payload.getMessageType() == MessageType.MESSAGE_TYPE_CMD_HEARTBEAT) {
			HeartBeat heartBeat = heartBeatFactory.buildDeseralizeHeartBeat(payload.getClientAddress(), payload.getClientCanonicalHostName(), payload.getData());
			messageHandler.onHeartBeatReceived(heartBeat);
			
		}else if(payload.getMessageType() == MessageType.MESSAGE_TYPE_DATA_BYTE_ARRAY) {
			MessageByteArray message = new MessageByteArray(payload.getData(), payload.getClientAddress(), payload.getClientCanonicalHostName());
			messageHandler.onMessageReceived(message);
			
		}else if(payload.getMessageType() == MessageType.MESSAGE_TYPE_DATA_UTF8_STRING) {
			
			MessageString message = new MessageString(stringDeseralizer.deserialize(payload.getData()), payload.getClientAddress(), payload.getClientCanonicalHostName());
			messageHandler.onMessageReceived(message);			
			
		}else if(payload.getMessageType() == MessageType.MESSAGE_TYPE_DATA_JAVA_OBJECT) {
			
			try {
				MessageObject message = new MessageObject(objectDeseralizer.deserialize(payload.getData()), payload.getClientAddress(), payload.getClientCanonicalHostName());
				messageHandler.onMessageReceived(message);				
			} catch (BroadCastSyncExceptionDeserializeData e) {
				
				log.error("error deserializing the java object received",e);
			}
		}else {
			log.error("recevied a message type not coded");
			
		}
		
		
		
	}
	
	/**
	 * clean the expired payload
	 * 
	 */
	private void cleanExpiredMessage() {
		
		ArrayList<String> expireds = new ArrayList<>();
		
		for (String messageID : payloads.keySet()) {
			Payload payload = payloads.get(messageID);
			
			//verify if the payload is expired then discard it
			if(payload.isExpired(config.getPayloadExpirationTime())) {
				expireds.add(messageID);
			}
		}
		
		for (String expired : expireds) {
			log.debug("messageID "+expired+" discarded");			
			payloads.remove(expired);
		}
		
	}

	
	public void startDatagramSequentializer() throws BroadCastSyncRuntimeException{
		stopped = false;		
		thread = new Thread(this);
		thread.setName("DatagramSequentializer");
		thread.start();
	}
	
	
	public void shutdown() {
		stopped = true;
		if(thread!=null) {
			thread.interrupt();			
		}
	}

	
}
