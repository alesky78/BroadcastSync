package it.spaghettisource.broadcastsync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.spaghettisource.broadcastsync.exception.BroadCastSyncException;
import it.spaghettisource.broadcastsync.exception.ExceptionFactory;
import it.spaghettisource.broadcastsync.i18n.FileMessageHelper;
import it.spaghettisource.broadcastsync.i18n.FileMessageRepository;
import it.spaghettisource.broadcastsync.infrastructure.DatagramPacketQueue;
import it.spaghettisource.broadcastsync.infrastructure.DatagramSequentializer;
import it.spaghettisource.broadcastsync.infrastructure.UdpServer;
import it.spaghettisource.broadcastsync.message.MessageProcessor;
import it.spaghettisource.broadcastsync.message.MessageProcessorLog;

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
	
	private BroadCastSyncConfig configuration;
	private ExceptionFactory exceptionFactory;
	
	private boolean initialized;
	private boolean started;	
		
	public BroadCastSyncManager() {
		super();
		initialized = false;
		started = false;
	}
	
	
	/**
	 * initialize the infrastructure but don't start the server
	 * using the default configuration
	 * 
	 * @throws BroadCastSyncException
	 */
	public void initialize(MessageProcessor messageProcessor){
		this.initialize(BroadCastSyncConfig.buildDefault(), messageProcessor);
	}

	/**
	 * initialize the infrastructure but don't start the server
	 * with a custom configuration
	 * 
	 * @throws BroadCastSyncException
	 */
	public void initialize(BroadCastSyncConfig config, MessageProcessor messageProcessor){
		
		log.info("init BroadCastSyncManager");
		
		//load default configuration
		configuration = config;
		
		//prepare the i18n messaged
		FileMessageRepository exceptionMessageRepository = new FileMessageRepository();
		exceptionMessageRepository.setMessageRepositoryBundleBaseName("i18n.exception-message");
		FileMessageHelper exceptionMessageHelper = new FileMessageHelper();
		exceptionMessageHelper.setMessageRepository(exceptionMessageRepository);

		//create the exception factory
		exceptionFactory = new ExceptionFactory(configuration,exceptionMessageHelper);
		
		//create the server infrastructure
		queue = new DatagramPacketQueue();
		sequentializer = new DatagramSequentializer(configuration, exceptionFactory, queue, messageProcessor);
		udpServer = new UdpServer(configuration, exceptionFactory,queue);
		
		//set the infrasrtucture as initialized
		initialized = true;
		
	}
	
	
	/**
	 * start the server infrastructure: the UDP server is started and the sequentialize will start to process the datagram 
	 * 
	 * @throws BroadCastSyncException
	 */
	public void start()  throws BroadCastSyncException{
		
		if(!initialized) {
			throw new IllegalStateException("the infrastructure BroadCastSync is not initialized, call the method initialize() first");
		}
		
		if(started) {
			BroadCastSyncException ex = exceptionFactory.getBroadCastSynAlreadyStarted();
			log.error(ex.getLocalizedMessage(),ex);
			throw ex;
		}
		
		String start = "\r\n" + 
				"   ___                   _______         __  ____             \r\n" + 
				"  / _ )_______  ___ ____/ / ___/__ ____ / /_/ __/_ _____  ____\r\n" + 
				" / _  / __/ _ \\/ _ `/ _  / /__/ _ `(_-</ __/\\ \\/ // / _ \\/ __/\r\n" + 
				"/____/_/  \\___/\\_,_/\\_,_/\\___/\\_,_/___/\\__/___/\\_, /_//_/\\__/ \r\n" + 
				"                                              /___/           \r\n" + 
				"";
		
		log.info(start);
		
		log.info("started the theads");
				
		sequentializer.startDatagramSequentializer();
		udpServer.startServer();
		
		started = true;
		
		log.info("started succesfully");
	}
	
	/**
	 * stop the server infrastructure: 
	 */
	public void shutdown() {
		
		if(!initialized) {
			throw new IllegalStateException("the infrastructure BroadCastSync is not initialized, call the method initialize() first");
		}
		
		log.info("shutdown BroadCastSyncManager");
		
		udpServer.shutdown();
		sequentializer.shutdown();
		
		queue.clear();
		
		log.info("shutdown BroadCastSyncManager completed succesfully");
		
	}
	
	public static void main(String[] args) throws Exception {
		BroadCastSyncManager manager = new BroadCastSyncManager();
		
		BroadCastSyncConfig conf = BroadCastSyncConfig.buildDefault();
		conf.setDevelopMode(true);
		
		manager.initialize(conf, new MessageProcessorLog());
		manager.start();
		
	}
	
	
	
}
