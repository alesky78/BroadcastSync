package it.spaghettisource.broadcastsync.infrastructure;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.spaghettisource.broadcastsync.BroadCastSyncConfig;
import it.spaghettisource.broadcastsync.exception.BroadCastSyncException;
import it.spaghettisource.broadcastsync.exception.ExceptionFactory;

/**
 * This class is responsible to receive the DatagramPacket 
 * 
 * @author Alessandro
 * @version 1.0
 */
public class UdpServer implements Runnable {

	private static Logger  log = LoggerFactory.getLogger(UdpServer.class);

	private boolean stopped = true;
	private Thread thread;
	
	private BroadCastSyncConfig config;
	private ExceptionFactory exceptionFactory;
	private DatagramPacketQueue queue;
	
	private DatagramSocket serverSocket;
	private String serverAddress;
	private String canonicalserverName;	
	

	public UdpServer(BroadCastSyncConfig config,ExceptionFactory exceptionFactory, DatagramPacketQueue queue){
			this.config = config;
			this.exceptionFactory = exceptionFactory;
			this.queue = queue;
	}


	@Override
	public void run() {

		log.debug("UdpServer started, address:"+serverAddress+" canonicalserverName:"+canonicalserverName);
		
		while (!stopped) {
			
			try {
				
				log.debug("listening for a new request");
				byte[] messageBuffer = new byte[config.getDatagramPacketBufferSize()];;
				DatagramPacket messagePacket = new DatagramPacket(messageBuffer, messageBuffer.length);
				serverSocket.receive(messagePacket);
				
				//filter the message send by itself
				if(!messagePacket.getAddress().getHostAddress().equals(serverAddress)) {
					//add the message in the queue and restart to listen for a new datagram
					log.debug("message received");
					queue.offer(messagePacket);		
				}else {
					log.debug("message filtered out, send from this machine");
				}
				
				
			}catch (InterruptedException e) {
				log.info("UdpServer server interrupted");
                break;
                
            }catch (Exception cause) {
				BroadCastSyncException ex = exceptionFactory.getUnexpectedException(cause);
				log.error(ex.getMessage(),ex);
				
			}
			
		}
		
		
	}
	
	public void startServer() throws BroadCastSyncException{
		try {
			stopped = false;
			
			InetAddress serverInfo = InetAddress.getLocalHost(); 
			serverAddress = serverInfo.getHostAddress();
			canonicalserverName = serverInfo.getCanonicalHostName();
			
			serverSocket = new DatagramSocket(config.getServerPort());
			
		} catch (SocketException cause) {
			BroadCastSyncException ex = exceptionFactory.geImpossibleStartDatagramSocket(cause);
			log.error(ex.getMessage(),ex);
			throw ex;
		} catch (UnknownHostException cause) {
			BroadCastSyncException ex = exceptionFactory.getUnexpectedException(cause);
			log.error(ex.getMessage(),ex);
			throw ex;
		}
		
		thread = new Thread(this);
		thread.setName("UdpServer");
		thread.start();
	}
	
	
	public void shutdown() {
		stopped = true;
		thread.interrupt();
	}
	
}
