package it.spaghettisource.broadcastsync.infrastructure;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.spaghettisource.broadcastsync.BroadCastSyncConfig;
import it.spaghettisource.broadcastsync.exception.BroadCastSyncException;
import it.spaghettisource.broadcastsync.exception.BroadCastSyncRuntimeException;
import it.spaghettisource.broadcastsync.exception.ExceptionFactory;

/**
 * The UdpServer is the entry point for all the DatagramPacket received.
 * It is responsible to open the DatagramSocket and verify that the socket can be properly be open
 * 
 * @author Alessandro D'Ottavio
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

		log.info("UdpServer thread started on address:"+serverAddress+" canonicalserverName:"+canonicalserverName);
		
		while (!stopped) {
			
			try {
				
				log.debug("listening for a new request");
				byte[] messageBuffer = new byte[config.getDatagramPacketBufferSize()];;
				DatagramPacket messagePacket = new DatagramPacket(messageBuffer, messageBuffer.length);
				serverSocket.receive(messagePacket);
				
				//filter the message send by itself
				//in develop mode the messages send by the same server are not filtered out
				if(!messagePacket.getAddress().getHostAddress().equals(serverAddress) || config.isDevelopMode()) {
					//add the message in the queue and restart to listen for a new datagram
					log.debug("DatagramPacket received");
					queue.offer(messagePacket);		
				}else {
					log.debug("DatagramPacket filtered out, sent from this machine");
				}
				
				
			}catch (InterruptedException e) {
				log.info("UdpServer server interrupted");
                break;
                
            }catch (PortUnreachableException cause) {
            	//this error should never be received, the DatagramSocket is used only to receive message
            	//it is never used to call and then received
            	BroadCastSyncRuntimeException ex = exceptionFactory.getUnexpectedException(cause);
				log.error(ex.getLocalizedMessage(),ex);
				
            }catch (SocketTimeoutException cause) {
            	//this error should never be received, we don use time out on server side
            	BroadCastSyncRuntimeException ex = exceptionFactory.getUnexpectedException(cause);
				log.error(ex.getLocalizedMessage(),ex);
            	
			} catch (IOException cause) {
				BroadCastSyncRuntimeException ex = exceptionFactory.getUnexpectedException(cause);
				log.error(ex.getLocalizedMessage(),ex);				 

			}
			
		}
		
		
	}
	
	public void startServer() throws BroadCastSyncRuntimeException{
		try {
			stopped = false;
			
			InetAddress serverInfo = InetAddress.getLocalHost(); 
			serverAddress = serverInfo.getHostAddress();
			canonicalserverName = serverInfo.getCanonicalHostName();
			
			serverSocket = new DatagramSocket(config.getServerPort());
			
		} catch (SocketException cause) {
			BroadCastSyncRuntimeException ex = exceptionFactory.getImpossibleOpenDatagramSocket(cause,config.getServerPort());
			log.error(ex.getLocalizedMessage(),ex);
			throw ex;
		} catch (UnknownHostException cause) {
			BroadCastSyncRuntimeException ex = exceptionFactory.getLocalHostNameCannotBeResolved(cause);
			log.error(ex.getLocalizedMessage(),ex);
			throw ex;
		}
		
		thread = new Thread(this);
		thread.setName("UdpServer");
		thread.start();
	}
	
	
	public void shutdown() {
		stopped = true;
		if(thread!=null) {
			thread.interrupt();			
		}
	}
	
}
