package it.spaghettisource.broadcastsync.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.spaghettisource.broadcastsync.message.HeartBeat;
import it.spaghettisource.broadcastsync.message.HeartBeatString;
import it.spaghettisource.broadcastsync.message.MessageByteArray;
import it.spaghettisource.broadcastsync.message.MessageObject;
import it.spaghettisource.broadcastsync.message.MessageString;

/**
 * simple implementation of the {@link MessageHandler} used for debug scope
 * 
 * @author Alessandro D'Ottavio
 * @version 1.0
 */
public class MessageHandlerLog implements MessageHandler {

	private static Logger  log = LoggerFactory.getLogger(MessageHandlerLog.class);

	
	
	@Override
	public void onMessageReceived(MessageByteArray message) {
		log.info("message byte array received");
		log.info("address: "+message.getClientAddress());
		log.info("name: "+message.getClientCanonicalHostName());
		log.info("data: "+new String(message.getData()));			
	}

	@Override
	public void onMessageReceived(MessageString message) {
		log.info("message string received");
		log.info("address: "+message.getClientAddress());
		log.info("name: "+message.getClientCanonicalHostName());
		log.info("data: "+message.getData());			
	}

	@Override
	public void onMessageReceived(MessageObject message) {
		log.info("message object received");
		log.info("address: "+message.getClientAddress());
		log.info("name: "+message.getClientCanonicalHostName());
		log.info("class: "+message.getData().getClass());		
		log.info("data: "+message.getData());	
		
	}

	@Override
	public void onHeartBeatReceived(HeartBeat heartBeat) {

		log.info("heartBeat received");
		log.info("address: "+heartBeat.getClientAddress());
		log.info("name: "+heartBeat.getClientCanonicalHostName());
		log.info("class: "+heartBeat.getClass().getName());		
		
		if(heartBeat instanceof HeartBeatString) {
			log.info("id: "+((HeartBeatString)heartBeat).getData());			
		
		
		}else if(heartBeat instanceof HeartBeat) {


		}
		

	}
	
}
