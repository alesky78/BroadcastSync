package it.spaghettisource.broadcastsync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.spaghettisource.broadcastsync.events.MessageReceivedEvent;
import it.spaghettisource.broadcastsync.events.MessageReceivedListener;
import it.spaghettisource.broadcastsync.exception.BroadCastSyncException;
import it.spaghettisource.broadcastsync.exception.ExceptionFactory;
import it.spaghettisource.broadcastsync.i18n.FileMessageHelper;
import it.spaghettisource.broadcastsync.i18n.FileMessageRepository;
import it.spaghettisource.broadcastsync.infrastructure.DatagramPacketQueue;
import it.spaghettisource.broadcastsync.infrastructure.DatagramSequentializer;
import it.spaghettisource.broadcastsync.infrastructure.UdpServer;

/**
 * The BroadCastSyncManager is responsible to initialize the infrastructure and start it
 * 
 * @author Alessandro  D'Ottavio
 * @version 1.0
 */
public class BroadCastSyncManager {

	private static Logger  log = LoggerFactory.getLogger(BroadCastSyncManager.class);
	
	private DatagramPacketQueue queue;
	private DatagramSequentializer sequentializer;
	private UdpServer udpServer;
		
	public BroadCastSyncManager() {
		super();
	}

	/**
	 * initialize the infrastructure without start it
	 * 
	 * @throws BroadCastSyncException
	 */
	public void init(MessageReceivedListener listener){
		
		log.info("init BroadCastSyncManager");
		
		//load default configuration
		BroadCastSyncConfig config = BroadCastSyncConfig.buildDefault();
		
		//prepare the i18n messaged
		FileMessageRepository exceptionMessageRepository = new FileMessageRepository();
		exceptionMessageRepository.setMessageRepositoryBundleBaseName("i18n.exception-message");
		FileMessageHelper exceptionMessageHelper = new FileMessageHelper();
		exceptionMessageHelper.setMessageRepository(exceptionMessageRepository);

		//create the exception factory
		ExceptionFactory exceptionFactory = new ExceptionFactory();
		exceptionFactory.setMessageHelper(exceptionMessageHelper);
		
		
		//create the server infrastructure
		queue = new DatagramPacketQueue();
		sequentializer = new DatagramSequentializer(config, exceptionFactory, queue, listener);
		udpServer = new UdpServer(config, exceptionFactory,queue);
		
	}
	
	
	/**
	 * start the server infrastructure: the UDP server is started and the sequentialize will start to process the datagram 
	 * 
	 * @throws BroadCastSyncException
	 */
	public void start()  throws BroadCastSyncException{
		
		String start = "\r\n" + 
				"   ___                   _______         __  ____             \r\n" + 
				"  / _ )_______  ___ ____/ / ___/__ ____ / /_/ __/_ _____  ____\r\n" + 
				" / _  / __/ _ \\/ _ `/ _  / /__/ _ `(_-</ __/\\ \\/ // / _ \\/ __/\r\n" + 
				"/____/_/  \\___/\\_,_/\\_,_/\\___/\\_,_/___/\\__/___/\\_, /_//_/\\__/ \r\n" + 
				"                                              /___/           \r\n" + 
				"";
		
		log.info(start);
				
		sequentializer.startDatagramSequentializer();
		udpServer.startServer();
		
		log.info("started succesfully");
	}
	
	/**
	 * stop the server infrastructure: 
	 */
	public void shutdown() {
		
		log.info("shutdown BroadCastSyncManager");
		
		udpServer.shutdown();
		sequentializer.shutdown();
		
		queue.clear();
		
	}
	
	public static void main(String[] args) throws Exception {
		BroadCastSyncManager manager = new BroadCastSyncManager();
		
		manager.init(manager.new TestListener());
		manager.start();
		
	}
	
	public class TestListener implements MessageReceivedListener{

		@Override
		public void onMessageReceived(MessageReceivedEvent event) {

			log.info("message received");
			log.info("address: "+event.getClientAddress());
			log.info("name: "+event.getClientCanonicalHostName());
			log.info("data: "+new String(event.getData()));			
			
		}
		
	}
	
	
}
