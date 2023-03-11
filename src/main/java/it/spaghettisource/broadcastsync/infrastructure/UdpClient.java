package it.spaghettisource.broadcastsync.infrastructure;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.spaghettisource.broadcastsync.BroadCastSyncConfig;
import it.spaghettisource.broadcastsync.exception.BroadCastSyncExceptionSerializeData;
import it.spaghettisource.broadcastsync.exception.BroadCastSyncRuntimeException;
import it.spaghettisource.broadcastsync.exception.ExceptionFactory;
import it.spaghettisource.broadcastsync.message.MessageType;
import it.spaghettisource.broadcastsync.serializer.ObjectSerializer;
import it.spaghettisource.broadcastsync.serializer.StringSeralizer;

/**
 * The UdpClient is the able to send message on the network.
 * 
 * @author Alessandro D'Ottavio
 * @version 1.0
 */
public class UdpClient {
	
	private static Logger  log = LoggerFactory.getLogger(UdpClient.class);
	
	private BroadCastSyncConfig config;
	private ExceptionFactory exceptionFactory;
	
	private StringSeralizer stringSeralizer;
	private ObjectSerializer<Serializable> objectSerializer;	
	
	public UdpClient(BroadCastSyncConfig config, ExceptionFactory exceptionFactory) {
		super();
		this.config = config;
		this.exceptionFactory = exceptionFactory;
		
		this.stringSeralizer = new StringSeralizer(exceptionFactory);
		this.objectSerializer = new ObjectSerializer<>(exceptionFactory);
		
	}

	public void sendMessage(byte[] data) throws BroadCastSyncRuntimeException{
		sendMessage(data, MessageType.MESSAGE_TYPE_DATA_BYTE_ARRAY);
	}

	public void sendMessage(String data) throws BroadCastSyncRuntimeException{
		sendMessage(stringSeralizer.serialize(data), MessageType.MESSAGE_TYPE_DATA_UTF8_STRING);
	}
	
	public <T extends Serializable> void sendMessage(T object) throws BroadCastSyncRuntimeException, BroadCastSyncExceptionSerializeData{
		sendMessage(objectSerializer.serialize(object), MessageType.MESSAGE_TYPE_DATA_JAVA_OBJECT);
	}
	
	private void sendMessage(byte[] data, int messageType) throws BroadCastSyncRuntimeException{
	
		//prepare the broadcast address
		InetAddress broadcastAddress;
		try {
			broadcastAddress = InetAddress.getByName(config.getBroadcastAddress());
		} catch (UnknownHostException cause) {
			BroadCastSyncRuntimeException ex = exceptionFactory.getBroadcastAddressCannotBeResolved(cause,config.getBroadcastAddress());
			log.error(ex.getLocalizedMessage(),ex);
			throw ex;
		}

		//prepare the packets
		DatagramPacket[] packets = DatagramPacketDataProtocol.buildDatagramPacket(broadcastAddress, config.getServerPort(), config.getDatagramPacketBufferSize(), messageType, data);
		
		//get the local host address
		InetAddress clientAddress;
		try {
			clientAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException cause) {
			BroadCastSyncRuntimeException ex = exceptionFactory.getLocalHostNameCannotBeResolved(cause);
			log.error(ex.getLocalizedMessage(),ex);
			throw ex;
		}

		//create the DatagramSocket and send the message on the network
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(config.getClientPort(),clientAddress);
			for (DatagramPacket datagramPacket : packets) {
			    socket.send(datagramPacket);
			}
		} catch (PortUnreachableException cause) {
        	//this error should never be received, the DatagramSocket send messages on broadcast
        	BroadCastSyncRuntimeException ex = exceptionFactory.getUnexpectedException(cause);
			log.error(ex.getLocalizedMessage(),ex);
			throw ex;			
			
        }catch (SocketException cause) {
			BroadCastSyncRuntimeException ex = exceptionFactory.getImpossibleOpenDatagramSocket(cause,config.getClientPort());
			log.error(ex.getLocalizedMessage(),ex);
			throw ex;

		} catch (IOException cause) {
        	BroadCastSyncRuntimeException ex = exceptionFactory.getUnexpectedException(cause);
			log.error(ex.getLocalizedMessage(),ex);
			throw ex;
			
		}finally {
			if(socket!=null) {
				socket.close();				
			}
			
		}
		
		
	}
	
}
