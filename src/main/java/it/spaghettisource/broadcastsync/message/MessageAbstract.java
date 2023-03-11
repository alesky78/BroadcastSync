package it.spaghettisource.broadcastsync.message;

/**
 * base implementation of a Message received to store to common data
 * 
 * @author Alessandro D'Ottavio
 * @version 1.0
 */
public abstract class MessageAbstract {

	protected final String clientAddress;
	protected final String clientCanonicalHostName;
	
	public MessageAbstract(String clientAddress, String clientCanonicalHostName) {
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
