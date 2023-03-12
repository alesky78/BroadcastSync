package it.spaghettisource.broadcastsync.message;

/**
 * Implementation of that HeartBeat that keep and extra String information
 * 
 * @author Alessandro D'Ottavio
 * @version 1.0
 */
public class HeartBeatString extends HeartBeat{
	
	private final String data;
	
	public HeartBeatString(String data, String clientAddress, String clientCanonicalHostName) {
		super(clientAddress, clientCanonicalHostName);
		this.data = data;
	}

	
	public String getData() {
		return data;
	}
	
}
