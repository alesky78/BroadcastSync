package it.spaghettisource.broadcastsync.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import it.spaghettisource.broadcastsync.BroadCastSyncConfig;
import it.spaghettisource.broadcastsync.exception.ExceptionFactory;
import it.spaghettisource.broadcastsync.handler.MessageHandler;
import it.spaghettisource.broadcastsync.handler.MessageHandlerLog;
import it.spaghettisource.broadcastsync.i18n.FileMessageHelper;
import it.spaghettisource.broadcastsync.i18n.FileMessageRepository;
import it.spaghettisource.broadcastsync.message.HeartBeatFactoryCommand;
import it.spaghettisource.broadcastsync.message.MessageType;
import it.spaghettisource.broadcastsync.serializer.ObjectSerializer;

public class DatagramSequentializerTest {

	private static InetAddress	address;
	private static ExceptionFactory exceptionFactory;
	private static DatagramPacketDataProtocol datagramPacketDataProtocol;
	private static HeartBeatFactoryCommand heartBeatFactoryCommand;
	private static BroadCastSyncConfig config;
	private static MessageHandler messageHandler;

	private static ObjectSerializer<Serializable> objectDeseralizer;
	
	private static DatagramSequentializer sequentializer;

	@BeforeAll
	public static void init() throws Exception {

		//prepare the config
		config = BroadCastSyncConfig.buildDefault();		
		
		//prepare the i18n messaged
		FileMessageRepository exceptionMessageRepository = new FileMessageRepository();
		exceptionMessageRepository.setMessageRepositoryBundleBaseName("i18n.exception-message");
		FileMessageHelper exceptionMessageHelper = new FileMessageHelper();
		exceptionMessageHelper.setMessageRepository(exceptionMessageRepository);

		//create the exception factory
		exceptionFactory = new ExceptionFactory(BroadCastSyncConfig.buildDefault(),exceptionMessageHelper);
		datagramPacketDataProtocol = new DatagramPacketDataProtocol(exceptionFactory);
		heartBeatFactoryCommand = new HeartBeatFactoryCommand();
		messageHandler = new MessageHandlerLog();

		objectDeseralizer = new ObjectSerializer<Serializable>(exceptionFactory);
		
		sequentializer = new DatagramSequentializer(config, exceptionFactory, null, heartBeatFactoryCommand, messageHandler);

		address = InetAddress.getLocalHost();
	}

	@DisplayName("process_CMD_heartbeat")
	@Test
	public void process_CMD_heartbeat_datagram_OK() throws Exception {

		DatagramPacket datagramPacket = DatagramPacketDataProtocol.buildCommandDatagramPacket(address, 1234, 256, MessageType.MESSAGE_TYPE_CMD_HEARTBEAT);

		Method method = DatagramSequentializer.class.getDeclaredMethod("process",DatagramPacket.class);
		method.setAccessible(true);
		method.invoke(sequentializer,datagramPacket);

	}
	
	@DisplayName("process_DATA_byte_array_datagram")
	@Test
	public void process_DATA_byte_array_datagram_OK() throws Exception {

		byte[] data = new byte[300];
		Arrays.fill(data, (byte) 1);
		
		DatagramPacket[] DatagramPackets = DatagramPacketDataProtocol.buildDatagramPacket(address, 1234, 256, MessageType.MESSAGE_TYPE_DATA_BYTE_ARRAY, data);

		Method method = DatagramSequentializer.class.getDeclaredMethod("process",DatagramPacket.class);
		method.setAccessible(true);
		for (DatagramPacket datagramPacket : DatagramPackets) {
			method.invoke(sequentializer,datagramPacket);			
		}
	}
	
	@DisplayName("process_DATA_utf8_datagram")
	@Test
	public void process_DATA_utf8_datagram_OK() throws Exception {

		DatagramPacket[] DatagramPackets = DatagramPacketDataProtocol.buildDatagramPacket(address, 1234, 256, MessageType.MESSAGE_TYPE_DATA_UTF8_STRING, "UTF-8 string".getBytes(StandardCharsets.UTF_8));

		Method method = DatagramSequentializer.class.getDeclaredMethod("process",DatagramPacket.class);
		method.setAccessible(true);
		for (DatagramPacket datagramPacket : DatagramPackets) {
			method.invoke(sequentializer,datagramPacket);			
		}
	}
	
	@DisplayName("process_DATA_java_obj_datagram")
	@Test
	public void process_DATA_java_obj_datagram_OK() throws Exception {

		byte[] data = objectDeseralizer.serialize(new Integer(100));
		
		DatagramPacket[] DatagramPackets = DatagramPacketDataProtocol.buildDatagramPacket(address, 1234, 256, MessageType.MESSAGE_TYPE_DATA_JAVA_OBJECT, data);

		Method method = DatagramSequentializer.class.getDeclaredMethod("process",DatagramPacket.class);
		method.setAccessible(true);
		for (DatagramPacket datagramPacket : DatagramPackets) {
			method.invoke(sequentializer,datagramPacket);			
		}
	}	
	
	@DisplayName("process_invalid_datagram_OK")
	@Test
	public void process_DATA_protocol_not_respected_OK() throws Exception {

		//create manual datagram invalid
		byte[] arr = new byte[256];
		Arrays.fill(arr, (byte) 1);
	    DatagramPacket packet = new DatagramPacket(arr, arr.length, address, 123);

		Method method = DatagramSequentializer.class.getDeclaredMethod("process",DatagramPacket.class);
		method.setAccessible(true);
		method.invoke(sequentializer,packet);			
		
	}

	@DisplayName("clean_expiredMessage")
	@Test	
	public void clean_expiredMessage() throws Exception{
		
		int EXPIRATION_TIME = 100;
		
		
		byte[] arr = new byte[256];
		Arrays.fill(arr, (byte) 1);
		
		DatagramPacket[] DatagramPackets = DatagramPacketDataProtocol.buildDatagramPacket(address, 1234, 50, MessageType.MESSAGE_TYPE_DATA_BYTE_ARRAY, arr);
		
		//create a fake payload, now it is stored in the memoty of the squentializer
		Payload payload = sequentializer.findPayloadOrBuildNewPayload(DatagramPackets[0], "message-Id-unit test clean_expiredMessage");
				
		
		//set the config to be sure that the packed is considered expired
		config.setPayloadExpirationTime(EXPIRATION_TIME);		
		config.setCleaningExpiredMessageIntervalTimeMillis(EXPIRATION_TIME);

		Thread.sleep(EXPIRATION_TIME*3);	//sleep little bit so we are sure all is expired	
		
		//call the method 
		Method method = DatagramSequentializer.class.getDeclaredMethod("performCleaningIfNeeded",null);
		method.setAccessible(true);
		method.invoke(sequentializer);
		
		assertEquals(0, sequentializer.findAmountOfPendingPayload());
	}

}
