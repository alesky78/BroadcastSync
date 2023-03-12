package it.spaghettisource.broadcastsync.handler;

import it.spaghettisource.broadcastsync.message.HeartBeat;
import it.spaghettisource.broadcastsync.message.MessageByteArray;
import it.spaghettisource.broadcastsync.message.MessageObject;
import it.spaghettisource.broadcastsync.message.MessageString;

/**
 * Interface for handling messages received by the server.
 * 
 * @author Alessandro
 * @version 1.0
 */
public interface MessageHandler {

	/**
	 * Invoked when a HeartBeat is received.
	 * @param heartBeat received.
	 */
	public void onHeartBeatReceived(HeartBeat heartBeat);
	
	/**
	 * Invoked when a message in byte array format is received.
	 * @param message received.
	 */
	public void onMessageReceived(MessageByteArray message);

	/**
	 * Invoked when a message in string format is received.
	 * @param message received.
	 */
	public void onMessageReceived(MessageString message);
	
	/**
	 * Invoked when a message in object format is received.
	 * @param message received.
	 */
	public void onMessageReceived(MessageObject message);
	
}
