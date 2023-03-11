package it.spaghettisource.broadcastsync;

import java.util.Locale;

/**
 * Configuration used by the BroadCastSync
 * 
 * @author Alessandro
 * @version 1.0 - base version
 *
 */
public class BroadCastSyncConfig {

	//server
	private int serverPort;
	
	//client
	private String broadcastAddress;
	
	//protocol
	private int datagramPacketBufferSize;
	
	//DatagramSequentializer
	private long payloadExpirationTime;
	private long cleaningExpiredMessageLoopTime;
	
	//i18n for the messages, like exception messages
	private String language;	
	private String country;	
	
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
	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;	
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	public Locale buildLocale() {
		return new Locale(language, country);
	}

	public static BroadCastSyncConfig buildDefault() {
		BroadCastSyncConfig config = new BroadCastSyncConfig();
		config.serverPort = 4445;
		config.datagramPacketBufferSize = 1024;
		config.broadcastAddress = "255.255.255.255";
		config.payloadExpirationTime = 3000;
		config.cleaningExpiredMessageLoopTime = 6000;
		
		config.language = Locale.getDefault().getLanguage();
		config.country = Locale.getDefault().getCountry();

		return config;
	}
	
}
