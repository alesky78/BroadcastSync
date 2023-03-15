package it.spaghettisource.broadcastsync.infrastructure;

/**
 * store all the chunks of a payload
 * 
 * 
 * @author Alessandro
 *
 */
public class Payload {

	private String clientAddress;
	private String clientCanonicalHostName;	
	private int totalPackets;
	private int messageType;	
	private long timeReceivedFirstChunk;

	private PayloadChunk[] chunks;

	public Payload(String clientAddress, String clientCanonicalHostName, int messageType, int totalPackets) {
		super();
		this.clientAddress = clientAddress;
		this.clientCanonicalHostName = clientCanonicalHostName;
		this.messageType = messageType;
		this.totalPackets = totalPackets;
		
		//if we are building the payload we received a chunk
		this.timeReceivedFirstChunk  = System.currentTimeMillis();
				
		this.chunks = new PayloadChunk[totalPackets];
	}

	public void addChunk(PayloadChunk chunk) {		
		chunks[chunk.getSequence()] = chunk;
	}
	
	public String getClientAddress() {
		return clientAddress;
	}

	public String getClientCanonicalHostName() {
		return clientCanonicalHostName;
	}

	public int getTotalPackets() {
		return totalPackets;
	}

	public int getMessageType() {
		return messageType;
	}	
	
	public byte[] getData(){
		
		int totalByte = totalByte();
		byte[] data = new byte[totalByte];
		
		byte[] chunkByte = null;
		int offset = 0;
		for (int i = 0; i < chunks.length; i++) {
			chunkByte = chunks[i].getData();
			System.arraycopy(chunkByte, 0, data, offset, chunkByte.length);
			offset += chunkByte.length;
		}
		
		return data;
	}
	

	/**
	 * @return true if all the chunks are received
	 */
	public boolean isCompleted() {
		return totalPackets == packetsReceived();
	}

	/**
	 * @return true if the time since we receive the first chunk is longer that the expiration time
	 */
	public boolean isExpired(long expirationTime) {
		return (System.currentTimeMillis() - timeReceivedFirstChunk) > expirationTime;
	}
	
	public int packetsReceived() {
		int counter = 0;
		for (int i = 0; i < chunks.length; i++) {
		    if (chunks[i] != null) {
		    	counter++;
		    }
		}
		return counter;
	}
	
	private int totalByte() {
		int counter = 0;
		for (int i = 0; i < chunks.length; i++) {
		    if (chunks[i] != null) {
		    	counter+= chunks[i].getData().length;
		    }
		}
		return counter;
	}
	
}
