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
import it.spaghettisource.broadcastsync.message.MessageByteArray;
import it.spaghettisource.broadcastsync.message.MessageObject;
import it.spaghettisource.broadcastsync.message.MessageString;
import it.spaghettisource.broadcastsync.message.MessageType;
import it.spaghettisource.broadcastsync.processor.MessageProcessor;
import it.spaghettisource.broadcastsync.serializer.ObjectSerializer;
import it.spaghettisource.broadcastsync.serializer.StringSeralizer;

/**
 * the role of the DatagramSequentializer is to reorganize the raw data received and propose them to the listener.
 * 
 * BroadCastSyn is able to subdivide a message sent in several chunk then this class has the responsibility to re sequentialize the data and compose the original message  
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
	private MessageProcessor messageProcessor;
	private DatagramPacketDataProtocol protocol;
	
	private StringSeralizer stringDeseralizer;
	private ObjectSerializer<Serializable> objectDeseralizer;
	
	public DatagramSequentializer(BroadCastSyncConfig config,ExceptionFactory exceptionFactory, DatagramPacketQueue queue, MessageProcessor messageProcessor){
		this.config = config;
		this.exceptionFactory = exceptionFactory;
		this.queue = queue;
		this.messageProcessor = messageProcessor;
		
		protocol = new DatagramPacketDataProtocol(exceptionFactory);
		payloads = new HashMap<String, Payload>();
		
		stringDeseralizer = new StringSeralizer(exceptionFactory);
		objectDeseralizer = new ObjectSerializer<Serializable>(exceptionFactory);
}
	
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
				if((System.currentTimeMillis()-lastCleaningLoopTime)> config.getCleaningExpiredMessageLoopTime()) {
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
	 * process the DatagramPacket received
	 * 
	 * @param datagram
	 */
	private void process(DatagramPacket datagram) {
		
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
				
				deserializeDataAndCallMessageProcessor(messageId, payload);
				
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
	 * and build the correct message used by the messageProcessor
	 * 
	 * @param messageId
	 * @param payload
	 */
	private void deserializeDataAndCallMessageProcessor(String messageId, Payload payload) {
		
		log.debug("message "+messageId+" complete, total packets:"+payload.getTotalPackets());
		
		if(payload.getMessageType() == MessageType.MESSAGE_TYPE_DATA_BYTE_ARRAY) {
			MessageByteArray message = new MessageByteArray(payload.getData(), payload.getClientAddress(), payload.getClientCanonicalHostName());
			messageProcessor.onMessageReceived(message);
			
		}else if(payload.getMessageType() == MessageType.MESSAGE_TYPE_DATA_UTF8_STRING) {
			
			MessageString message = new MessageString(stringDeseralizer.deserialize(payload.getData()), payload.getClientAddress(), payload.getClientCanonicalHostName());
			messageProcessor.onMessageReceived(message);			
			
		}else if(payload.getMessageType() == MessageType.MESSAGE_TYPE_DATA_JAVA_OBJECT) {
			
			try {
				MessageObject message = new MessageObject(objectDeseralizer.deserialize(payload.getData()), payload.getClientAddress(), payload.getClientCanonicalHostName());
				messageProcessor.onMessageReceived(message);				
			} catch (BroadCastSyncExceptionDeserializeData e) {
				
				log.error("error deserializing the java object received",e);
			}
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
