package it.spaghettisource.broadcastsync;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import it.spaghettisource.broadcastsync.handler.MessageHandlerLog;

public class BoradCastSyncManagerTest {

	
	@DisplayName("start_BoradCastSyncManager_OK_Test")
	@Test
	public void start_BoradCastSyncManager_OK_Test() {
		
		try {
			BroadCastSyncManager manager = new BroadCastSyncManager();
			manager.initialize(new MessageHandlerLog());
			manager.start();
			manager.shutdown();			
		}catch (Exception ex) {
			
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
