package it.spaghettisource.broadcastsync.exception;

/**
 * Thrown to indicate that a DatagramPacket doesn't respect the protocol defined by BroadCastSync 
 * 
 * @author Alessandro D'Ottavio
 * @version 1.0
 */
public class BroadCastSyncExceptionDeserializeData extends BroadCastSyncException {

	public BroadCastSyncExceptionDeserializeData(Throwable cause,String errorMessage,Object... messageParameters ) {
		super(cause, errorMessage, messageParameters);
	}

	public BroadCastSyncExceptionDeserializeData(String errorMessage,Object...  messageParameters ) {
		super(errorMessage, messageParameters);
	}		
	
}
