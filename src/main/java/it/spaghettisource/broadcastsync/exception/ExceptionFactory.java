package it.spaghettisource.broadcastsync.exception;

import java.net.SocketException;

import it.spaghettisource.broadcastsync.i18n.I18NMessageHelper;

/**
 * Factory class used to create exception, this class allow to format the correct error message
 * 
 * @author Alessando D'Ottavio
 * @version 1.0
 */
public class ExceptionFactory {

	private static Object[] EMPTY_PARAMETERS = new Object[] {};
	
	private I18NMessageHelper messageHelper;
	
	public void setMessageHelper(I18NMessageHelper messageHelper) {
		this.messageHelper = messageHelper;
	}
	
	public BroadCastSyncException getUnexpectedException(Throwable cause){		
		return getException(cause, "exception.UnexpectedException", EMPTY_PARAMETERS);
	}
	
	public BroadCastSyncException geImpossibleStartDatagramSocket(SocketException cause){		
		return getException(cause, "exception.server.impossibleStartDatagramSocket", EMPTY_PARAMETERS);
	}
	
	
	/**
	 * support method to create an exception
	 * 
	 * @param errorMessage
	 * @param messageParameters
	 * @return
	 */
	private BroadCastSyncException getException(String errorMessage,Object... messageParameters ){
		BroadCastSyncException ex = new BroadCastSyncException(errorMessage, messageParameters);
		ex.setMessageHelper(messageHelper);
		return ex;
	}

	/**
	 * support method to create an exception wiht the root cause
	 * 
	 * @param cause
	 * @param errorMessage
	 * @param messageParameters
	 * @return
	 */	
	private BroadCastSyncException getException(Throwable cause,String errorMessage,Object... messageParameters ){
		BroadCastSyncException ex = new BroadCastSyncException(cause,errorMessage, messageParameters);
		ex.setMessageHelper(messageHelper);
		return ex;
	}












			
}
