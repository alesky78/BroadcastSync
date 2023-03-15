package it.spaghettisource.broadcastsync;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.spaghettisource.broadcastsync.exception.BroadCastSyncException;
import it.spaghettisource.broadcastsync.exception.BroadCastSyncExceptionSerializeData;
import it.spaghettisource.broadcastsync.exception.BroadCastSyncRuntimeException;
import it.spaghettisource.broadcastsync.exception.ExceptionFactory;
import it.spaghettisource.broadcastsync.handler.MessageHandler;
import it.spaghettisource.broadcastsync.i18n.FileMessageHelper;
import it.spaghettisource.broadcastsync.i18n.FileMessageRepository;
import it.spaghettisource.broadcastsync.infrastructure.DatagramPacketQueue;
import it.spaghettisource.broadcastsync.infrastructure.DatagramSequentializer;
import it.spaghettisource.broadcastsync.infrastructure.HeartbeatEmitter;
import it.spaghettisource.broadcastsync.infrastructure.UdpClient;
import it.spaghettisource.broadcastsync.infrastructure.UdpServer;
import it.spaghettisource.broadcastsync.message.HeartBeatFactory;
import it.spaghettisource.broadcastsync.message.HeartBeatFactoryCommand;

/**
 * The BroadCastSyncManager is responsible to initialize the infrastructure and start it
 * Moreover it wrap the method to send the messages.
 * 
 * can exist only one instance of the BroadCastSyncManager for JVM, it is responsibility of the application that is using BroadCastSync to ensure to create and use only one instance of the BroadCastSyncManager
 * 
 * a client application that want to use this libraries has to implement only the interface {@link MessageHandler} to manage the received messages, all the rest is managed automatically by the library.
 * 
 * start the BroadCastSync networks on one node is very simple:
 * <code>
 			BroadCastSyncManager manager = new BroadCastSyncManager();
			manager.initialize(new MessageHandlerLog());	//substitute whit you specific implementation of the message handler
			manager.start();
 * </code>
 * 
 * when the application shout down instead the resources have o be free up
 * <code>
			manager.shutdown();
 * </code>
 * 
 * SEND MESSAGES ON THE NETWORK
 * Last important point, the BroadCastSyncManager wrapp the method to send message os the network, avoiding to the developer to know how is working the internal infrastructure of BroadCastSync library.
 * There are 3 methot able to senda  message on the networ:
 * 1 - {@link BroadCastSyncManager#sendMessage(byte[])}
 * 2 - {@link BroadCastSyncManager#sendMessage(String)}
 * 3 - {@link BroadCastSyncManager#sendMessage(Serializable)}
 * 
 * see the jave doc of each method to have detail over its implementation or limits if any
 * 
 * 
 * 
 * @author Alessandro  D'Ottavio
 * @version 1.0
 */
public class BroadCastSyncManager {

	private static Logger  log = LoggerFactory.getLogger(BroadCastSyncManager.class);
	
	private DatagramPacketQueue queue;
	private DatagramSequentializer sequentializer;
	private UdpServer udpServer;
	
	private HeartBeatFactory heartBeatFactor;
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
	
	

	public void initialize(MessageHandler messageProcessor){
		this.initialize(BroadCastSyncConfig.buildDefault(),new HeartBeatFactoryCommand(), messageProcessor);
	}
	
	public void initialize(BroadCastSyncConfig configuration, MessageHandler messageProcesso){
		this.initialize(configuration,new HeartBeatFactoryCommand(), messageProcesso);
	}
	
	public void initialize(HeartBeatFactory heartBeatFactory, MessageHandler messageProcessor){
		this.initialize(BroadCastSyncConfig.buildDefault(),heartBeatFactory, messageProcessor);		
	}

	/**
	 * initialize the infrastructure but don't start the server
	 * with a custom configuration
	 * 
	 * @throws BroadCastSyncException
	 */
	public void initialize(BroadCastSyncConfig configuration, HeartBeatFactory heartBeatFactory, MessageHandler messageProcessor){
		
		log.info("init BroadCastSyncManager");
		
		//store the configuration
		this.configuration = configuration;
		this.heartBeatFactor = heartBeatFactory;		
		
		//prepare the i18n messaged
		FileMessageRepository exceptionMessageRepository = new FileMessageRepository();
		exceptionMessageRepository.setMessageRepositoryBundleBaseName("i18n.exception-message");
		FileMessageHelper exceptionMessageHelper = new FileMessageHelper();
		exceptionMessageHelper.setMessageRepository(exceptionMessageRepository);

		//create the exception factory
		exceptionFactory = new ExceptionFactory(configuration,exceptionMessageHelper);
		
		//create the infrastructure
		queue = new DatagramPacketQueue();
		sequentializer = new DatagramSequentializer(configuration, exceptionFactory, queue, heartBeatFactor, messageProcessor);
		udpServer = new UdpServer(configuration, exceptionFactory,queue);
		
		udpClient = new UdpClient(configuration, exceptionFactory);
		

		heartbeatEmitter = new HeartbeatEmitter(configuration, heartBeatFactor, exceptionFactory, udpClient);
		
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
			//if there is an error stop the thread started			
			if(configuration.isEnableHeartbeat()){
				heartbeatEmitter.shutdown();
			}
			
			udpClient.shutdown();
			
			udpServer.shutdown();
			sequentializer.shutdown();
			queue.clear();
			
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
	 * Send a String on the networks, the string are deserialized/serialized in a byte array using the UTF-8 encoding
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
	
	
}
