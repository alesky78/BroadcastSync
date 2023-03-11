package it.spaghettisource.broadcastsync.exception;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Locale;

import it.spaghettisource.broadcastsync.BroadCastSyncConfig;
import it.spaghettisource.broadcastsync.i18n.I18NMessageHelper;

/**
 * Factory class used to create exception, this class allow to format the correct error message
 * 
 * @author Alessando D'Ottavio
 * @version 1.0
 */
public class ExceptionFactory {

	private static final Object[] EMPTY_PARAMETERS = new Object[] {};
	
	private BroadCastSyncConfig config;
	private I18NMessageHelper messageHelper;
	private Locale locale;
	

	public ExceptionFactory(BroadCastSyncConfig config, I18NMessageHelper messageHelper) {
		super();
		this.config = config;
		this.messageHelper = messageHelper;
		this.locale = config.buildLocale();
		
	}
	
	public BroadCastSyncRuntimeException getUnexpectedException(Throwable cause){		
		return getRuntimeException(cause, locale, "exception.UnexpectedException", EMPTY_PARAMETERS);
	}
	
	public BroadCastSyncRuntimeException getBroadCastSynAlreadyStarted(){		
		return getRuntimeException(locale, "exception.broadCastSyn.alreadyStarted", EMPTY_PARAMETERS);
	}
	
	
	public BroadCastSyncRuntimeException getImpossibleOpenDatagramSocket(SocketException cause, int socketPort){		
		return getRuntimeException(cause, locale, "exception.server.impossibleOpenDatagramSocket", new Object[] {socketPort});
	}

	public BroadCastSyncRuntimeException getLocalHostNameCannotBeResolved(UnknownHostException cause){		
		return getRuntimeException(cause, locale, "exception.server.localHostNameCannotBeResolved", EMPTY_PARAMETERS);
	}	
	
	public BroadCastSyncExceptionDataProtocolNotRespected getBroadCastSyncExceptionDatagramDataProtocolNotRespected(Exception cause) {
		BroadCastSyncExceptionDataProtocolNotRespected ex = new BroadCastSyncExceptionDataProtocolNotRespected(cause, "exception.datagram.protocolNotRespected=", EMPTY_PARAMETERS);
		ex.setMessageHelper(messageHelper);
		ex.setLocale(locale);
		return ex;
	}	
	
	/**
	 * support method to create an exception
	 * 
	 * @param errorMessage
	 * @param messageParameters
	 * @return
	 */
	private BroadCastSyncException getException(Locale locale, String errorMessage,Object... messageParameters ){
		BroadCastSyncException ex = new BroadCastSyncException(errorMessage, messageParameters);
		ex.setMessageHelper(messageHelper);
		ex.setLocale(locale);
		return ex;
	}

	/**
	 * support method to create an exception whit the root cause
	 * 
	 * @param cause
	 * @param errorMessage
	 * @param messageParameters
	 * @return
	 */	
	private BroadCastSyncException getException(Throwable cause,Locale locale,String errorMessage,Object... messageParameters ){
		BroadCastSyncException ex = new BroadCastSyncException(cause,errorMessage, messageParameters);
		ex.setMessageHelper(messageHelper);
		ex.setLocale(locale);		
		return ex;
	}
	
	
	/**
	 * support method to create an unchecked exceptions
	 * 
	 * @param errorMessage
	 * @param messageParameters
	 * @return
	 */
	private BroadCastSyncRuntimeException getRuntimeException(Locale locale, String errorMessage,Object... messageParameters ){
		BroadCastSyncRuntimeException ex = new BroadCastSyncRuntimeException(errorMessage, messageParameters);
		ex.setMessageHelper(messageHelper);
		ex.setLocale(locale);
		return ex;
	}

	/**
	 * support method to create an unchecked exceptions whit the root cause
	 * 
	 * @param cause
	 * @param errorMessage
	 * @param messageParameters
	 * @return
	 */	
	private BroadCastSyncRuntimeException getRuntimeException(Throwable cause,Locale locale,String errorMessage,Object... messageParameters ){
		BroadCastSyncRuntimeException ex = new BroadCastSyncRuntimeException(cause,errorMessage, messageParameters);
		ex.setMessageHelper(messageHelper);
		ex.setLocale(locale);		
		return ex;
	}














			
}
