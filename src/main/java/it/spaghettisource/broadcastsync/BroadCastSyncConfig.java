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
	
	/**
	 * the develop mode flag, is create to support implementation debug and unit test
	 * activating the develop mode:
	 *  - disable the filter out of the message same from the same machine or the server, mean that the instance will receive the message sent by itself 
	 */
	private boolean developMode;
	
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
	
	public boolean isDevelopMode() {
		return developMode;
	}

	public void setDevelopMode(boolean developMode) {
		this.developMode = developMode;
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
		
		config.developMode = false;

		return config;
	}
	
}
