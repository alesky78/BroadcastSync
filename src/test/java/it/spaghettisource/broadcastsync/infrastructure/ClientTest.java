package it.spaghettisource.broadcastsync.infrastructure;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.spaghettisource.broadcastsync.BroadCastSyncConfig;
import it.spaghettisource.broadcastsync.message.MessageType;

public class ClientTest {

	private static Logger  log = LoggerFactory.getLogger(ClientTest.class);
	
	private DatagramSocket socket;
	private InetAddress broadcastAddress;	
	
	
	@DisplayName("send message")
	@Test
	void sendMessage() throws Exception {
		
		BroadCastSyncConfig config = BroadCastSyncConfig.buildDefault();
		
		broadcastAddress = InetAddress.getByName(config.getBroadcastAddress());		
		String message = "Un primo esempio di test non lungo anzi lungolungolungolungolungolungolungolungolungolungolungolungolungolungolungolungolungolungolungolungolungolungolungolungolungolungolungolungolungolungolungolungolungolungolungolungolungo";
		
		DatagramPacket[] packets = DatagramPacketDataProtocol.buildDatagramPacket(broadcastAddress, config.getServerPort(), config.getDatagramPacketBufferSize(), MessageType.MESSAGE_TYPE_DATA_BYTE_ARRAY, message.getBytes());
		
		//send the messages
		InetAddress clientAddress = InetAddress.getLocalHost();
		socket = new DatagramSocket(4446,clientAddress);
		
		for (DatagramPacket datagramPacket : packets) {
		    socket.send(datagramPacket);
		}
		
		socket.close();
		
	}
	
	
	
}
