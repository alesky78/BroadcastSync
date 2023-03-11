package it.spaghettisource.broadcastsync.exception;

/**
 * Thrown to indicate that a DatagramPacket doesn't respect the protocol defined by BroadCastSync 
 * 
 * @author Alessandro D'Ottavio
 * @version 1.0
 */
public class BroadCastSyncExceptionDataProtocolNotRespected extends BroadCastSyncException {

	public BroadCastSyncExceptionDataProtocolNotRespected(Throwable cause,String errorMessage,Object... messageParameters ) {
		super(cause, errorMessage, messageParameters);
	}

	public BroadCastSyncExceptionDataProtocolNotRespected(String errorMessage,Object...  messageParameters ) {
		super(errorMessage, messageParameters);
	}		
	
}
