package it.spaghettisource.broadcastsync.message;

/**
 * interface extended by all the HeartBeat Messages
 * 
 * @author Alessandro D'Ottavio
 * @version 1.0
 */
public class HeartBeat {

	protected final String clientAddress;
	protected final String clientCanonicalHostName;
	
	
	public HeartBeat(String clientAddress, String clientCanonicalHostName) {
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
