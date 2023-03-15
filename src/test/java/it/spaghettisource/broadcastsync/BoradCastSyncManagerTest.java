package it.spaghettisource.broadcastsync;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import it.spaghettisource.broadcastsync.exception.BroadCastSyncExceptionSerializeData;
import it.spaghettisource.broadcastsync.exception.BroadCastSyncRuntimeException;
import it.spaghettisource.broadcastsync.handler.MessageHandlerLog;
import it.spaghettisource.broadcastsync.message.HeartBeatFactory;
import it.spaghettisource.broadcastsync.message.HeartBeatFactoryInstanceId;

public class BoradCastSyncManagerTest {


	@DisplayName("integeration_send_message_OK_Test")
	@Test
	public void integeration_send_message_OK_Test() throws BroadCastSyncRuntimeException, BroadCastSyncExceptionSerializeData {

		try {
			BroadCastSyncManager manager = new BroadCastSyncManager();

			BroadCastSyncConfig conf = BroadCastSyncConfig.buildDefault();
			conf.setDevelopMode(true);
			conf.setEnableHeartbeat(true);

			HeartBeatFactory heartBeatFactory = new HeartBeatFactoryInstanceId("ID instance");

			manager.initialize(conf, heartBeatFactory, new MessageHandlerLog());
			manager.start();

			manager.sendMessage("send byte array message".getBytes());
			manager.sendMessage("send string message");
			manager.sendMessage(new Integer(9999999));

			manager.shutdown();

		}catch (Exception ex) {
			fail("unexpected exception");		
		}

	}


	@DisplayName("start_BoradCastSyncManager_OK_Test")
	@Test
	public void start_BoradCastSyncManager_OK_Test() {

		try {
			BroadCastSyncManager manager = new BroadCastSyncManager();
			manager.initialize(new MessageHandlerLog());
			manager.start();
			manager.shutdown();			
		}catch (Exception ex) {
			fail("unexpected exception");		
		}

	}

	@DisplayName("not_initialized_BoradCastSyncManager_KO_Test")
	@Test
	public void not_initialized_start_BoradCastSyncManager_KO_Test() {

		assertThrows(IllegalStateException.class, () -> {
			BroadCastSyncManager manager = new BroadCastSyncManager();
			manager.start();			
		});
	}	

	@DisplayName("already_started_BoradCastSyncManager_KO_Test")
	@Test
	public void already_started_BoradCastSyncManager_KO_Test() {

		BroadCastSyncManager manager = new BroadCastSyncManager();
		try {
			manager.initialize(new MessageHandlerLog());
			manager.start();
			manager.start();		
			fail("expected exception");			
		}catch (Exception ex) {
			manager.shutdown();
		}		
	}	

	@DisplayName("shutdown_BoradCastSyncManager_OK_Test")
	@Test
	public void shutdown_BoradCastSyncManager_OK_Test() {

		try {
			BroadCastSyncManager manager = new BroadCastSyncManager();
			manager.initialize(new MessageHandlerLog());
			manager.start();
			manager.shutdown();
		}catch (Exception ex) {
			fail("unexpected exception"+ex.getMessage());
		}
	}	

	@DisplayName("shutdown_not_initialized_BoradCastSyncManager_KO_Test")
	@Test
	public void not_initialized_shutdown_BoradCastSyncManager_KO_Test() {

		assertThrows(IllegalStateException.class, () -> {
			BroadCastSyncManager manager = new BroadCastSyncManager();
			manager.shutdown();			
		});
	}



}
