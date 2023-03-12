package it.spaghettisource.broadcastsync.message;

/**
 * implementation of a Message that receive a message as byte array
 * 
 * @author Alessandro D'Ottavio
 * @version 1.0
 */
public class MessageByteArray extends Message{
	
	private final byte[] data;

	public MessageByteArray(byte[] data, String clientAddress, String clientCanonicalHostName) {
		super(clientAddress, clientCanonicalHostName);
		this.data = data;

	}

	public byte[] getData() {
		return data;
	}

	
}
