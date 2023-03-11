package it.spaghettisource.broadcastsync.exception;


import java.util.Locale;

import it.spaghettisource.broadcastsync.i18n.I18NMessageHelper;

/**
 * Superclass of all Exceptions used
 * 
 * @author Alessandro D'Ottavio
 * @version 1.0
 */
public class BroadCastSyncException extends RuntimeException {

	protected Locale locale;
	protected String errorMessage;  
	protected Object[] messageParameters;
	
	protected I18NMessageHelper messageHelper;
	
	public BroadCastSyncException(Throwable cause,String errorMessage,Object... messageParameters ) {
		super(cause);
		this.errorMessage = errorMessage;
		this.messageParameters = messageParameters;
	}

	public BroadCastSyncException(String errorMessage,Object...  messageParameters ) {
		super();
		this.errorMessage = errorMessage;
		this.messageParameters = messageParameters;
	}

	public void setMessageHelper(I18NMessageHelper messageHelper) {
		this.messageHelper = messageHelper;
	}
	
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	@Override
	public String getMessage(){		
		return messageHelper.getFormattedMessageI18N(errorMessage, messageParameters);		 
	}

	@Override
	public String getLocalizedMessage() {		
		return messageHelper.getFormattedMessageI18N(locale, errorMessage, messageParameters);
	}
		
	
}
