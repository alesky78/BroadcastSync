package it.spaghettisource.broadcastsync.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.spaghettisource.broadcastsync.exception.BroadCastSyncExceptionDeserializeData;
import it.spaghettisource.broadcastsync.exception.BroadCastSyncExceptionSerializeData;
import it.spaghettisource.broadcastsync.exception.ExceptionFactory;

/**
 * serialize and deserialize java objects
 * 
 * @author Alessandro D'Ottavio
 * @version 1.0
 *
 * @param <T>
 */
public class ObjectSerializer<T extends Serializable> extends SeralizerAbstract<T> {

	private static Logger  log = LoggerFactory.getLogger(ObjectSerializer.class);
	
	public ObjectSerializer(ExceptionFactory exceptionFactory) {
		super(exceptionFactory);
	}

	@Override
	public byte[] serialize(T object)  throws BroadCastSyncExceptionSerializeData {
		try {
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
			objectStream.writeObject(object);
			return byteStream.toByteArray();			
		}catch (Exception cause) {
			BroadCastSyncExceptionSerializeData ex = exceptionFactory.getImpossibleSerializeObject(cause);
			throw ex;			
		}

	}

	@Override
	public T deserialize(byte[] bytes) throws BroadCastSyncExceptionDeserializeData {
		try {
			ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
			ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream);
			return (T) objectInputStream.readObject();
		}catch (Exception cause) {
			BroadCastSyncExceptionDeserializeData ex = exceptionFactory.getImpossibleDeserializeObject(cause);
			throw ex;	
		}			
	}

	
}
