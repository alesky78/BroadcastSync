package it.spaghettisource.broadcastsync.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import it.spaghettisource.broadcastsync.BroadCastSyncConfig;
import it.spaghettisource.broadcastsync.exception.BroadCastSyncExceptionDataProtocolNotRespected;
import it.spaghettisource.broadcastsync.exception.ExceptionFactory;
import it.spaghettisource.broadcastsync.i18n.FileMessageHelper;
import it.spaghettisource.broadcastsync.i18n.FileMessageRepository;
import it.spaghettisource.broadcastsync.message.MessageType;

public class DatagramPacketDataProtocolTest {

	private static InetAddress	address;
	private static ExceptionFactory exceptionFactory;
	private static DatagramPacketDataProtocol datagramPacketDataProtocol;
	
	@BeforeAll
	public static void init() throws Exception {
		
		//prepare the i18n messaged
		FileMessageRepository exceptionMessageRepository = new FileMessageRepository();
		exceptionMessageRepository.setMessageRepositoryBundleBaseName("i18n.exception-message");
		FileMessageHelper exceptionMessageHelper = new FileMessageHelper();
		exceptionMessageHelper.setMessageRepository(exceptionMessageRepository);

		//create the exception factory
		exceptionFactory = new ExceptionFactory(BroadCastSyncConfig.buildDefault(),exceptionMessageHelper);
		datagramPacketDataProtocol = new DatagramPacketDataProtocol(exceptionFactory);
		
		address = InetAddress.getLocalHost();
		
	}
	
	@DisplayName("test_serialize_deserialize_UUID")
	@Test
	public void test_serialize_deserialize_UUID() {
		
		UUID uuid = UUID.randomUUID();
		
		byte[] serialized = DatagramPacketDataProtocol.serializeUUID(uuid);
		String originalUUID = DatagramPacketDataProtocol.deserializeUUID(serialized);
		
		assertEquals(uuid.toString(), originalUUID);
		
		
	}

	@DisplayName("test_buildDatagramPacket_size_1_OK")
	@Test
	public void test_buildDatagramPacket_size_1_OK() {
		
		DatagramPacket[] packets = DatagramPacketDataProtocol.buildDatagramPacket(address, 1234, 256, MessageType.MESSAGE_TYPE_DATA_BYTE_ARRAY, new byte[1]);
		assertEquals(1, packets.length);
	}
	
	@DisplayName("test_buildDatagramPacket_size_more_then_1_OK")
	@Test
	public void test_buildDatagramPacket_size_more_then_1_OK() {
		
		DatagramPacket[] packets = DatagramPacketDataProtocol.buildDatagramPacket(address, 1234, 256, MessageType.MESSAGE_TYPE_DATA_BYTE_ARRAY, new byte[300]);
		assertEquals(true, packets.length>1);
	}	
	
	@DisplayName("test_buildCommandDatagramPacket_OK")
	@Test
	public void test_buildCommandDatagramPacket_OK() {
		
		DatagramPacket packet = DatagramPacketDataProtocol.buildCommandDatagramPacket(address, 1234, 256, MessageType.MESSAGE_TYPE_CMD_HEARTBEAT);
		assertNotNull(packet);
		
	}
	
	@DisplayName("test_buildCommandDatagramPacket_OK")
	@Test
	public void test_analyse_DatagramPacket_OK() {
		
		DatagramPacket packet = DatagramPacketDataProtocol.buildCommandDatagramPacket(address, 1234, 256, MessageType.MESSAGE_TYPE_DATA_BYTE_ARRAY);
		
		try {
			datagramPacketDataProtocol.analyzed(packet);
		} catch (BroadCastSyncExceptionDataProtocolNotRespected e) {
			fail("unexpected exception"+e.getMessage());
		}		
	}	
	
	@DisplayName("test_buildCommandDatagramPacket_OK")
	@Test
	public void test_analyse_DatagramPacket_KO() {
		
		//create manual datagram invalid
		byte[] arr = new byte[256];
		Arrays.fill(arr, (byte) 1);
	    DatagramPacket packet = new DatagramPacket(arr, arr.length, address, 123);
		
		try {
			datagramPacketDataProtocol.analyzed(packet);
			fail("expected exception");			
		} catch (BroadCastSyncExceptionDataProtocolNotRespected e) {

		}		
	}
	
}
