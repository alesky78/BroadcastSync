package it.spaghettisource.broadcastsync.processor;

import it.spaghettisource.broadcastsync.message.MessageByteArray;
import it.spaghettisource.broadcastsync.message.MessageObject;
import it.spaghettisource.broadcastsync.message.MessageString;

/**
 * 
 * 
 * @author Alessandro D'Ottavio
 * @version 1.0
 */
public interface MessageProcessor {

	
	public void onMessageReceived(MessageByteArray message);
	
	public void onMessageReceived(MessageString message);
	
	public void onMessageReceived(MessageObject message);
	
	
}
