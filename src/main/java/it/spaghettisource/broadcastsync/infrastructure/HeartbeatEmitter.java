package it.spaghettisource.broadcastsync.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.spaghettisource.broadcastsync.BroadCastSyncConfig;
import it.spaghettisource.broadcastsync.exception.BroadCastSyncRuntimeException;
import it.spaghettisource.broadcastsync.exception.ExceptionFactory;

/**
 * The HeartbeatEmitter is responsible to send the Heartbeat on the network.
 * 
 * @author Alessandro D'Ottavio
 * @version 1.0
 */
public class HeartbeatEmitter implements Runnable{

	private static Logger  log = LoggerFactory.getLogger(HeartbeatEmitter.class);
	
	private boolean stopped = true;
	private Thread thread;
	
	
	private BroadCastSyncConfig config;
	private ExceptionFactory exceptionFactory;
	private UdpClient udpClient;
	
	public HeartbeatEmitter(BroadCastSyncConfig config,ExceptionFactory exceptionFactory, UdpClient udpClient) {
		super();
		this.config = config;
		this.exceptionFactory = exceptionFactory;
		this.udpClient = udpClient;
	}

	@Override
	public void run() {
		
		log.info("HeartbeatEmitter thread started");
		
		while (!stopped) {
			try {

				//send hearbeat
				udpClient.sendMessage("HeartBeat");
				
				//wait till next hearbeat execution
				Thread.sleep(config.getHeartbeatIntervalTimeMillis());
				
			} catch (InterruptedException e) {
				log.info("HeartbeatEmitter interrupted");
                break; 
	          
			} catch (Exception cause) {
            	BroadCastSyncRuntimeException ex = exceptionFactory.getUnexpectedException(cause);
				log.error(ex.getLocalizedMessage(),ex);
			}
		}
	}
	
	public void startHeartbeatEmitter() throws BroadCastSyncRuntimeException{
		stopped = false;	
		thread = new Thread(this);
		thread.setName("HeartbeatEmitter");	
		thread.start();
	}
	
	
	public void shutdown() {
		stopped = true;
		if(thread!=null) {
			thread.interrupt();
		}
	}
	
	
}
