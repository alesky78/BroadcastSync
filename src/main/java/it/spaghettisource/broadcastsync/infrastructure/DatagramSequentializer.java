package it.spaghettisource.broadcastsync.infrastructure;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.spaghettisource.broadcastsync.BroadCastSyncConfig;
import it.spaghettisource.broadcastsync.exception.BroadCastSyncException;
import it.spaghettisource.broadcastsync.exception.ExceptionFactory;
import it.spaghettisource.broadcastsync.message.MessageByteArray;
import it.spaghettisource.broadcastsync.message.MessageProcessor;

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
	
	private BroadCastSyncConfig config;
	private ExceptionFactory exceptionFactory;
	private DatagramPacketQueue queue;
	
	private MessageProcessor messageProcessor;
	
	private DatagramProtocol protocol;
	
	private Map<String,Payload> payloads;

	public DatagramSequentializer(BroadCastSyncConfig config,ExceptionFactory exceptionFactory, DatagramPacketQueue queue, MessageProcessor messageProcessor){
		this.config = config;
		this.exceptionFactory = exceptionFactory;
		this.queue = queue;
		this.messageProcessor = messageProcessor;
		
		protocol = new DatagramProtocol();
		payloads = new HashMap<String, Payload>();
}
	
	@Override
	public void run() {

		log.debug("DatagramSequentializer started");
		
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
				BroadCastSyncException ex = exceptionFactory.getUnexpectedException(cause);
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
			protocol.analize(datagram);
			
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
				
				if(payload.getMessageType() == MessageType.MESSAGE_TYPE_DATA_BYTE_ARRAY) {
					
					log.debug("message "+messageId+" complete, total packets:"+payload.getTotalPackets());
					MessageByteArray event = new MessageByteArray(payload.getData(), payload.getClientAddress(), payload.getClientCanonicalHostName());
					messageProcessor.onMessageReceived(event);
				}
				
			}

		}catch (Exception e) {
			log.error("error pocessing the datagram",e);
			
			//if there is an error processing this message discard the payload, now is impossible rebuild the message
			if(messageId!=null) {
				payloads.remove(messageId);
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

	
	public void startDatagramSequentializer() throws BroadCastSyncException{
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
