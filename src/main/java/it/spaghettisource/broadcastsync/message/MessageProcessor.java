package it.spaghettisource.broadcastsync.message;


/**
 * 
 * 
 * @author Alessandro D'Ottavio
 * @version 1.0
 */
public interface MessageProcessor {

	
	public void onMessageReceived(MessageByteArray event);
	
}
