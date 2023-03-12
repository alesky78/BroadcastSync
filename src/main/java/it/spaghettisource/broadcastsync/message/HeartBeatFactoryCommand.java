package it.spaghettisource.broadcastsync.message;


/**
 * The most simple of the heartbeat, it is a command without data
 * 
 * @author Alessandro D'Ottavio
 * @version 1.0
 */
public class HeartBeatFactoryCommand implements HeartBeatFactory {

	
	@Override
	public boolean isCommandHeartBeat() {
		return true;
	}
	
	@Override
	public byte[] buildSerializeHeartBeat() {
		return null;
	}

	@Override
	public HeartBeat buildDeseralizeHeartBeat(String clientAddress, String clientCanonicalHostName, byte[] data) {
		return new HeartBeat(clientAddress, clientCanonicalHostName);
	}



}
