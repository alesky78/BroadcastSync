package it.spaghettisource.broadcastsync.message;

/**
 * implementation of a Message that receive a message as byte array
 * 
 * @author Alessandro D'Ottavio
 * @version 1.0
 */
public class MessageString extends  MessageAbstract{
	
	private final String data;

	public MessageString(String data, String clientAddress, String clientCanonicalHostName) {
		super(clientAddress, clientCanonicalHostName);
		this.data = data;

	}

	public String getData() {
		return data;
	}

	
}
