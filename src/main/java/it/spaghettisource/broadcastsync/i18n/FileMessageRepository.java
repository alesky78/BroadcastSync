package it.spaghettisource.broadcastsync.i18n;

import java.util.Locale;
import java.util.ResourceBundle;


/**
 * <code>FileMessageRepository</code> provides utilities to get messages from a resource bundle element.
 *
 * 
 * @author Alessandro D'Ottavio
 * @version 1.0
 *
 */
public class FileMessageRepository {

	
	private String resourceBundleFileName;

	/**
	 * set the base bundle name to use to internationalize the message
	 * 
	 * @param errorMessageBundleFile
	 */
	public void setMessageRepositoryBundleBaseName(String resourceBundleFileName) {
		this.resourceBundleFileName = resourceBundleFileName; 
	}

	/**
	 * get a message by id using the default locale configured in the JVM
	 * 
	 * @param messageId
	 * @return
	 */
	public  String getMessageById(String messageId){

        ResourceBundle messages;
        
        messages = ResourceBundle.getBundle(resourceBundleFileName);
        
		return messages.getString(messageId);
		
	}

	/**
	 * get a message by id using the locale configured in the JVM
	 * 
	 * @param messageId
	 * @param locale
	 * @return
	 */
	public  String getMessageById(String messageId,Locale locale){

        ResourceBundle messages;
        messages = ResourceBundle.getBundle(resourceBundleFileName,locale);
        
		return messages.getString(messageId);
		
	}
	
	
}
