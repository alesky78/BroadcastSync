package it.spaghettisource.broadcastsync.events;

public class MessageReceivedEvent {
	
	private byte[] data;
	private String clientAddress;
	private String clientCanonicalHostName;
	
	public MessageReceivedEvent(byte[] data, String clientAddress, String clientCanonicalHostName) {
		super();
		this.data = data;
		this.clientAddress = clientAddress;
		this.clientCanonicalHostName = clientCanonicalHostName;
	}

	public byte[] getData() {
		return data;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public String getClientCanonicalHostName() {
		return clientCanonicalHostName;
	}
	

}
