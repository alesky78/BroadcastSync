package it.spaghettisource.broadcastsync.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * simple implementation of the {@link MessageProcessor} used for debug scope
 * 
 * @author Alessandro D'Ottavio
 * @version 1.0
 */
public class MessageProcessorLog implements MessageProcessor {

	private static Logger  log = LoggerFactory.getLogger(MessageProcessorLog.class);

	public void onMessageReceived(MessageByteArray event) {
		
		log.info("message received");
		log.info("address: "+event.getClientAddress());
		log.info("name: "+event.getClientCanonicalHostName());
		log.info("data: "+new String(event.getData()));			
		
	}
	
}
