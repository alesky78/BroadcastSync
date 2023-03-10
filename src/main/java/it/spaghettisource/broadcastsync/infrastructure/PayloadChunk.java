package it.spaghettisource.broadcastsync.infrastructure;

/**
 * Store the data related to a single chunk of a message subdivided in multiple payload
 * 
 * @author Alessandro
 *
 */
public class PayloadChunk {

	private int sequence;
	private byte[] data;
	
	
	public PayloadChunk(int sequence, byte[] data) {
		super();
		this.sequence = sequence;
		this.data = data;
	}

	public int getSequence() {
		return sequence;
	}

	public byte[] getData() {
		return data;
	}
	
	
}
