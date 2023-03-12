package it.spaghettisource.broadcastsync.message;

/**
 * class extended by all the Messages
 * 
 * @author Alessandro D'Ottavio
 * @version 1.0
 */
public class Message {

	protected final String clientAddress;
	protected final String clientCanonicalHostName;
	
	public Message(String clientAddress, String clientCanonicalHostName) {
		super();
		this.clientAddress = clientAddress;
		this.clientCanonicalHostName = clientCanonicalHostName;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public String getClientCanonicalHostName() {
		return clientCanonicalHostName;
	}
	
}
