package it.spaghettisource.broadcastsync.message;

import it.spaghettisource.broadcastsync.serializer.StringSeralizer;

/**
 * Implementation of that send a fixed String data,
 * 
 * This is used for example in the case you want to associate an ID for each instance that participate in the BroadCastSync network
 * in this case when the HeartBeat message arrive to the other clients, it brings also this extra information
 * 
 * @author Alessandro D'Ottavio
 * @version 1.0
 */
public class HeartBeatFactoryInstanceId implements HeartBeatFactory {

	private String instanceId;
	private StringSeralizer serializer;
	
	public HeartBeatFactoryInstanceId(String instanceId) {
		super();
		this.instanceId = instanceId;
		this.serializer = new StringSeralizer(null);
	}

	@Override
	public boolean isCommandHeartBeat() {
		return false;
	}
	
	@Override
	public byte[] buildSerializeHeartBeat() {		
		return serializer.serialize(instanceId);
	}

	@Override
	public HeartBeatString buildDeseralizeHeartBeat(String clientAddress, String clientCanonicalHostName, byte[] data) {
		
		String caller = "";
		try {
			caller = serializer.deserialize(data);			
		}catch (Exception e) {
		}

		
		return new HeartBeatString(caller,clientAddress, clientCanonicalHostName);
	}


}
