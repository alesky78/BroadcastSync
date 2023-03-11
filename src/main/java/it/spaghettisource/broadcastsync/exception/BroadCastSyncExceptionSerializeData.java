package it.spaghettisource.broadcastsync.exception;

/**
 * Thrown to indicate that a DatagramPacket doesn't respect the protocol defined by BroadCastSync 
 * 
 * @author Alessandro D'Ottavio
 * @version 1.0
 */
public class BroadCastSyncExceptionSerializeData extends BroadCastSyncException {

	public BroadCastSyncExceptionSerializeData(Throwable cause,String errorMessage,Object... messageParameters ) {
		super(cause, errorMessage, messageParameters);
	}

	public BroadCastSyncExceptionSerializeData(String errorMessage,Object...  messageParameters ) {
		super(errorMessage, messageParameters);
	}		
	
}
