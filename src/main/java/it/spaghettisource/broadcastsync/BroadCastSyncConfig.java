package it.spaghettisource.broadcastsync;

/**
 * Configuration used by the BroadCastSync
 * 
 * @author Alessandro
 * @version 1.0 - base version
 *
 */
public class BroadCastSyncConfig {

	private int serverPort;
	private int datagramPacketBufferSize;
	private String broadcastAddress;
	private long payloadExpirationTime;
	private long cleaningExpiredMessageLoopTime;	

	public BroadCastSyncConfig() {
		super();
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public int getDatagramPacketBufferSize() {
		return datagramPacketBufferSize;
	}

	public void setDatagramPacketBufferSize(int datagramPacketBufferSize) {
		this.datagramPacketBufferSize = datagramPacketBufferSize;
	}

	public String getBroadcastAddress() {
		return broadcastAddress;
	}

	public void setBroadcastAddress(String broadcastAddress) {
		this.broadcastAddress = broadcastAddress;
	}

	public long getPayloadExpirationTime() {
		return payloadExpirationTime;
	}

	public void setPayloadExpirationTime(long payloadExpirationTime) {
		this.payloadExpirationTime = payloadExpirationTime;
	}
	
	public long getCleaningExpiredMessageLoopTime() {
		return cleaningExpiredMessageLoopTime;
	}

	public void setCleaningExpiredMessageLoopTime(long cleaningExpiredMessageLoopTime) {
		this.cleaningExpiredMessageLoopTime = cleaningExpiredMessageLoopTime;
	}

	public static BroadCastSyncConfig buildDefault() {
		BroadCastSyncConfig config = new BroadCastSyncConfig();
		config.serverPort = 4445;
		config.datagramPacketBufferSize = 1024;
		config.broadcastAddress = "255.255.255.255";
		config.payloadExpirationTime = 3000;
		config.cleaningExpiredMessageLoopTime = 6000;

		return config;
	}
	
}
