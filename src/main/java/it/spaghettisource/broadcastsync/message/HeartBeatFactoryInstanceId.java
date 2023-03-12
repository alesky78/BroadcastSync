package it.spaghettisource.broadcastsync.message;

import java.nio.charset.StandardCharsets;

/**
 * Implementation of that send a fixed String data,
 * 
 * This is used for example in the case you want to associate an ID for each instance that participate in the BroadCastSync network
 * 
 * @author Alessandro D'Ottavio
 * @version 1.0
 */
public class HeartBeatFactoryInstanceId implements HeartBeatFactory {

	private String instanceId;
	
	public HeartBeatFactoryInstanceId(String instanceId) {
		super();
		this.instanceId = instanceId;
	}

	@Override
	public boolean isCommandHeartBeat() {
		return false;
	}
	
	@Override
	public byte[] buildSerializeHeartBeat() {
		return instanceId.getBytes(StandardCharsets.UTF_8);
	}

	@Override
	public HeartBeatString buildDeseralizeHeartBeat(String clientAddress, String clientCanonicalHostName, byte[] data) {
		return new HeartBeatString(instanceId,clientAddress, clientCanonicalHostName);
	}


}
