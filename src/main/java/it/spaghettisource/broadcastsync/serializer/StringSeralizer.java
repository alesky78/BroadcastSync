package it.spaghettisource.broadcastsync.serializer;

import java.nio.charset.StandardCharsets;

import it.spaghettisource.broadcastsync.exception.ExceptionFactory;

/**
 * serialize and deserialize String objects
 * 
 * @author Alessandro D'Ottavio
 * @version 1.0
 *
 */
public class StringSeralizer extends SeralizerAbstract<String> {


	public StringSeralizer(ExceptionFactory exceptionFactory) {
		super(exceptionFactory);
	}

	@Override
	public byte[] serialize(String data) {

		return data.getBytes(StandardCharsets.UTF_8);
	}

	@Override
	public String deserialize(byte[] bytes) {

		return new String(bytes, StandardCharsets.UTF_8);
	}

}
