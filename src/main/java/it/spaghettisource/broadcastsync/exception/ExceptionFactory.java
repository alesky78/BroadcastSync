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
	
	public BroadCastSyncException getUnexpectedException(Throwable cause){		
		return getException(cause, locale, "exception.UnexpectedException", EMPTY_PARAMETERS);
	}
	
	public BroadCastSyncException getBroadCastSynAlreadyStarted(){		
		return getException(locale, "exception.broadCastSyn.alreadyStarted", EMPTY_PARAMETERS);
	}
	
	
	public BroadCastSyncException getImpossibleOpenDatagramSocket(SocketException cause, int socketPort){		
		return getException(cause, locale, "exception.server.impossibleOpenDatagramSocket", new Object[] {socketPort});
	}

	public BroadCastSyncException getLocalHostNameCannotBeResolved(UnknownHostException cause){		
		return getException(cause, locale, "exception.server.localHostNameCannotBeResolved", EMPTY_PARAMETERS);
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
	 * support method to create an exception wiht the root cause
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












			
}
