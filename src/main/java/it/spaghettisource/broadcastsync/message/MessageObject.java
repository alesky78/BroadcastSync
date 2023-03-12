package it.spaghettisource.broadcastsync.message;

/**
 * implementation of a Message that receive a message as byte array
 * 
 * @author Alessandro D'Ottavio
 * @version 1.0
 */
public class MessageObject extends Message{
	
	private final Object data;

	public MessageObject(Object data, String clientAddress, String clientCanonicalHostName) {
		super(clientAddress, clientCanonicalHostName);
		this.data = data;

	}

	public Object getData() {
		return data;
	}

	
}
