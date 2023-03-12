package it.spaghettisource.broadcastsync.message;

/**
 * define the message types supported by the infrastructure
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class MessageType {

	public final static int MESSAGE_TYPE_CMD_HEARTBEAT = 0;
	
	public final static int MESSAGE_TYPE_DATA_BYTE_ARRAY = 1;
	public final static int MESSAGE_TYPE_DATA_UTF8_STRING = 2;
	public final static int MESSAGE_TYPE_DATA_JAVA_OBJECT = 3;	

}
