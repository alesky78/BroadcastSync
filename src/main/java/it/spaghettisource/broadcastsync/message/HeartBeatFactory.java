package it.spaghettisource.broadcastsync.message;

/**
 * interface used to crete the heartBeat Message
 * 
 * 
 * @author Alessandro D'Ottavio
 * @version 1.0
 */
public interface HeartBeatFactory {

	
	/**
	 * The Command heartbean are sent wihtout data part
	 * 
	 * @return
	 */
	public boolean isCommandHeartBeat();
	
	/**
	 * serialize the data that must be send on the network
	 * 
	 * @param data to serialize
	 * @return byte array
	 */
	public byte[] buildSerializeHeartBeat();
	
	public HeartBeat buildDeseralizeHeartBeat(String clientAddress, String clientCanonicalHostName, byte[] data);
	
	
}
