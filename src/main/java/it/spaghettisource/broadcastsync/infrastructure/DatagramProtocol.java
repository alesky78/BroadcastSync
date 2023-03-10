package it.spaghettisource.broadcastsync.infrastructure;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * this class define the rules of the chunk protocol, and know how to analyze it 
 * 
 * |---------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------|
 * | HEADER                                                                                            | DATA                                                                       |
 * |--------------------|----------------------|---------------------------|---------------------------|-------------------------|--------------------------------------------------|
 * | messageID          | message type         | total packets             | sequence of packet        | amount of data          | data                                             |
 * |--------------------|----------------------|---------------------------|---------------------------|-------------------------|--------------------------------------------------|
 * | byte[] 16 byte     | int 4 byte           | int 4 byte                | int 4 byte                | int 4 byte              | byte[] x byte config dependent                   |
 * | it is a UUID       | Like ECHO, DATA      | numbers of datagram       | position of the datagram  | lenght of the data      | data                                             |
 * |                    |                      |                           | first position is 0       |                         |                                                  |
 * |--------------------|----------------------|---------------------------|---------------------------|-------------------------|--------------------------------------------------|
 * 
 * the supported message types are defined in the class {@link MessageType}
 * 
 * @author Alessandro
 *
 */
public class DatagramProtocol {
	
	
	//payload data
	private String messageID;
	private int messageType;
	private int totalPackets;

	//payload chunk data	
	private int sequence;
	private int dataAmount;
	private byte[] data;
	
	/**
	 * Analyze a DatagramPacket, after that is possible to call the getter to obtain the info of this payload 
	 * 
	 * @param datagram
	 */
	public void analize(DatagramPacket datagram) {
		byte[] rawData = datagram.getData();
		
		messageID =       deserializeUUID(ByteBuffer.wrap(rawData, 0, 16).array());
		messageType = 	  ByteBuffer.wrap(rawData, 16, 4).getInt();
		totalPackets = 	  ByteBuffer.wrap(rawData, 20, 4).getInt();
		sequence = 		  ByteBuffer.wrap(rawData, 24, 4).getInt();
		dataAmount = 	  ByteBuffer.wrap(rawData, 28, 4).getInt();		
		
	    data =  new byte[dataAmount];				
	    System.arraycopy(rawData, 32, data, 0, dataAmount);		
		
	}
	
	/**
	 * @return the messageID associated at this chunk
	 */
	public String getMessageId() {
		return messageID;
	}

	/**
	 * @return the type of message
	 */
	public int getMessageType() {
		return messageType;
	}

	/**
	 * @return the total packet expected
	 */
	public int getTotalPackets() {
		return totalPackets;
	}
	
	/**
	 * @return the position of this datagram considering the total payload
	 */
	public int getSequence() {
		return sequence;
	}

	/**
	 * @return the PayloadChunk related to the datagram analyzed
	 */
	public PayloadChunk buildPayloadChunk() {
		return new PayloadChunk(sequence, data);
	}


	public static DatagramPacket[] buildDatagram(InetAddress address, int port, int datagramPacketBufferSize, int msgType, byte[] dataByte ) {
		
		int dataLength = dataByte.length;
		
		//calculate the total amount of datagram needed to send this message
		int packetDataSize = datagramPacketBufferSize - 32;
		int totalPackets = (int) Math.ceil((double) dataLength / packetDataSize);

		// prepare all the datagrma
		UUID uuid = UUID.randomUUID();
		DatagramPacket[] packets = new DatagramPacket[totalPackets];
		byte[] data;
		byte[] partialData;		
		for (int i = 0; i < totalPackets; i++) {
			
		    int offset = i * packetDataSize;
		    int length = Math.min(packetDataSize, dataLength - offset);
		    
		    data = new byte[length + 32];
		    
		    System.arraycopy(serializeUUID(uuid), 0, data, 0, 16);	//fill messageID
		    
		    partialData = ByteBuffer.allocate(4).putInt(msgType).array(); //fill message type
		    System.arraycopy(partialData, 0, data, 16, 4);
		    
		    partialData = ByteBuffer.allocate(4).putInt(totalPackets).array(); //fill total packets
		    System.arraycopy(partialData, 0, data, 20, 4);
		    
		    partialData = ByteBuffer.allocate(4).putInt(i).array();	//fill sequence number
		    System.arraycopy(partialData, 0, data, 24, 4);
		    
		    partialData = ByteBuffer.allocate(4).putInt(length).array(); //fill data amount
		    System.arraycopy(partialData, 0, data, 28, 4);
		    
		    System.arraycopy(dataByte, offset, data, 32, length); //fill data	    
		    
		    packets[i] = new DatagramPacket(data, data.length, address, port);
		}
		
		return packets;
	}
	
	private static String deserializeUUID(byte[] uuidBytes) {
		long mostSignificantBits = 0;
		long leastSignificantBits = 0;
		for (int i = 0; i < 8; i++) {
			mostSignificantBits |= (long) (uuidBytes[i] & 0xFF) << (8 * (7 - i));
			leastSignificantBits |= (long) (uuidBytes[i + 8] & 0xFF) << (8 * (7 - i));
		}
		return new UUID(mostSignificantBits, leastSignificantBits).toString();

	}
	
	private static byte[] serializeUUID(UUID uuid) {
		long mostSignificantBits = uuid.getMostSignificantBits();
		long leastSignificantBits = uuid.getLeastSignificantBits();
		byte[] uuidByteArray = new byte[16];
		for (int i = 0; i < 8; i++) {
			uuidByteArray[i] = (byte) (mostSignificantBits >>> 8 * (7 - i));
			uuidByteArray[i + 8] = (byte) (leastSignificantBits >>> 8 * (7 - i));
		}

		return uuidByteArray;
	}
	
}
