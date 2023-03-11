package it.spaghettisource.broadcastsync.serializer;

import it.spaghettisource.broadcastsync.exception.BroadCastSyncExceptionDeserializeData;
import it.spaghettisource.broadcastsync.exception.BroadCastSyncExceptionSerializeData;

/**
 * User to serialize specific message type
 * 
 * @author Alessandro D'Ottavio
 * @version
 * @param <T> that be serizalied in byte array and viceversa
 */
public interface Serializer<T> {

	/**
	 * serialize the data that  must be send on the network
	 * 
	 * @param data to serialize
	 * @return byte array
	 */
	public byte[] serialize(T data) throws BroadCastSyncExceptionSerializeData;
	
	/**
	 * deserialize the byte array received on the network to the orignial object
	 * 
	 * @param bytes
	 * @return
	 */
	public T deserialize(byte[] bytes) throws BroadCastSyncExceptionDeserializeData;
	
	
	
}
