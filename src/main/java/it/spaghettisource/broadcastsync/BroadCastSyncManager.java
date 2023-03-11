package it.spaghettisource.broadcastsync;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.spaghettisource.broadcastsync.exception.BroadCastSyncException;
import it.spaghettisource.broadcastsync.exception.BroadCastSyncExceptionSerializeData;
import it.spaghettisource.broadcastsync.exception.BroadCastSyncRuntimeException;
import it.spaghettisource.broadcastsync.exception.ExceptionFactory;
import it.spaghettisource.broadcastsync.handler.MessageHandler;
import it.spaghettisource.broadcastsync.handler.MessageHandlerLog;
import it.spaghettisource.broadcastsync.i18n.FileMessageHelper;
import it.spaghettisource.broadcastsync.i18n.FileMessageRepository;
import it.spaghettisource.broadcastsync.infrastructure.DatagramPacketQueue;
import it.spaghettisource.broadcastsync.infrastructure.DatagramSequentializer;
import it.spaghettisource.broadcastsync.infrastructure.HeartbeatEmitter;
import it.spaghettisource.broadcastsync.infrastructure.UdpClient;
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
	
	private HeartbeatEmitter heartbeatEmitter;
	
	private UdpClient udpClient;
	
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
	public void initialize(MessageHandler messageProcessor){
		this.initialize(BroadCastSyncConfig.buildDefault(), messageProcessor);
	}

	/**
	 * initialize the infrastructure but don't start the server
	 * with a custom configuration
	 * 
	 * @throws BroadCastSyncException
	 */
	public void initialize(BroadCastSyncConfig config, MessageHandler messageProcessor){
		
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
		
		//create the infrastructure
		queue = new DatagramPacketQueue();
		sequentializer = new DatagramSequentializer(configuration, exceptionFactory, queue, messageProcessor);
		udpServer = new UdpServer(configuration, exceptionFactory,queue);
		
		udpClient = new UdpClient(config, exceptionFactory);
		
		heartbeatEmitter = new HeartbeatEmitter(config, exceptionFactory, udpClient);
		
		//set the infrasrtucture as initialized
		initialized = true;
		
	}
	
	
	/**
	 * start the server infrastructure: the UDP server is started and the sequentialize will start to process the datagram 
	 * 
	 * @throws BroadCastSyncException
	 */
	public void start() throws BroadCastSyncRuntimeException{
		
		if(!initialized) {
			throw new IllegalStateException("the infrastructure BroadCastSync is not initialized, call the method initialize() first");
		}
		
		if(started) {
			BroadCastSyncRuntimeException ex = exceptionFactory.getBroadCastSynAlreadyStarted();
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
		
		try {
			sequentializer.startDatagramSequentializer();
			udpServer.startServer();
			
			udpClient.startClient();
			
			if(configuration.isEnableHeartbeat()){
				heartbeatEmitter.startHeartbeatEmitter();				
			}
			
		}catch (BroadCastSyncRuntimeException cause) {
			//if there is an error stop the thread started if any			
			sequentializer.shutdown();
			queue.clear();
			udpServer.shutdown();
			
			log.error("emergency shutdown, all the started thread are interrupted",cause);
			throw cause;
		}

		
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
		
		if(configuration.isEnableHeartbeat()){
			heartbeatEmitter.shutdown();
		}
		
		udpClient.shutdown();
		
		udpServer.shutdown();
		sequentializer.shutdown();
		queue.clear();
		
		started = false;
		
		log.info("shutdown BroadCastSyncManager completed succesfully");
		
	}
	
	
	/**
	 * Send a byte[] on the networks
	 * 
	 * @param data
	 * @throws BroadCastSyncRuntimeException
	 */
	public void sendMessage(byte[] data) throws BroadCastSyncRuntimeException{
		udpClient.sendMessage(data);
	}

	/**
	 * Send a String on the networks, the string are deserialized/serialized in UTF-8
	 * 
	 * @param data
	 * @throws BroadCastSyncRuntimeException
	 */
	public void sendMessage(String data) throws BroadCastSyncRuntimeException{
		udpClient.sendMessage(data);
	}
	
	/**
	 * Send a java Object that extends Serializable on the networks
	 * There are several limits to consider when sending a serialized object in a byte array from one application and deserializing it in another:
	 * 
	 * Class version: 
	 * if the version of the class being deserialized is different from the version of the class that was serialized, errors may occur during deserialization. 
	 * It is important to ensure that the class versions are compatible.
	 * 
	 * Data types: 
	 * if the data types of the fields in the serialized class are not compatible with the data types of the fields in the deserialized class, errors may occur during deserialization. 
	 * For example, if a field in the serialized class is of type long and the corresponding field in the deserialized class is of type int, an error may occur.
	 * 
	 * Java version: 
	 * if the application deserializing the object is running on a different version of Java than the application that serialized the object, compatibility issues may arise.
	 * 
	 * Character encoding: 
	 * if the serialized class contains strings or other character-based data, it is important to ensure that the character encodings are compatible between the two applications.
	 * 
	 * 
	 * @param object
	 * @throws BroadCastSyncRuntimeException
	 * @throws BroadCastSyncExceptionSerializeData
	 */
	public <T extends Serializable> void sendMessage(T object) throws BroadCastSyncRuntimeException, BroadCastSyncExceptionSerializeData{
		udpClient.sendMessage(object);
	}


	public static void main(String[] args) throws Exception {
		BroadCastSyncManager manager = new BroadCastSyncManager();
		
		BroadCastSyncConfig conf = BroadCastSyncConfig.buildDefault();
		conf.setDevelopMode(true);
		conf.setEnableHeartbeat(true);
		
		manager.initialize(conf, new MessageHandlerLog());
		manager.start();

		manager.sendMessage("ciao stringa byte array".getBytes());
		manager.sendMessage("ciao stringa");
		manager.sendMessage(new Integer(9999999));			

		manager.shutdown();
		Thread.sleep(1000);
		manager.start();		
//		
//		manager.sendMessage("ciao stringa byte array".getBytes());
//		manager.sendMessage("ciao stringa");
//		manager.sendMessage(new Integer(9999999));
		
	}
	
	
	
}
