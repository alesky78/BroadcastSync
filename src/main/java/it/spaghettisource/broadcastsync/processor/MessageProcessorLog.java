package it.spaghettisource.broadcastsync.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.spaghettisource.broadcastsync.message.MessageByteArray;
import it.spaghettisource.broadcastsync.message.MessageObject;
import it.spaghettisource.broadcastsync.message.MessageString;

/**
 * simple implementation of the {@link MessageProcessor} used for debug scope
 * 
 * @author Alessandro D'Ottavio
 * @version 1.0
 */
public class MessageProcessorLog implements MessageProcessor {

	private static Logger  log = LoggerFactory.getLogger(MessageProcessorLog.class);

	@Override
	public void onMessageReceived(MessageByteArray message) {
		log.info("message received");
		log.info("address: "+message.getClientAddress());
		log.info("name: "+message.getClientCanonicalHostName());
		log.info("data: "+new String(message.getData()));			
	}

	@Override
	public void onMessageReceived(MessageString message) {
		log.info("message received");
		log.info("address: "+message.getClientAddress());
		log.info("name: "+message.getClientCanonicalHostName());
		log.info("data: "+message.getData());			
	}

	@Override
	public void onMessageReceived(MessageObject message) {
		log.info("message received");
		log.info("address: "+message.getClientAddress());
		log.info("name: "+message.getClientCanonicalHostName());
		log.info("class: "+message.getData().getClass());		
		log.info("data: "+message.getData());	
		
	}
	
}
