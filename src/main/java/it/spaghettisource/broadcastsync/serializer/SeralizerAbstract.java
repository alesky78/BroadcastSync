package it.spaghettisource.broadcastsync.serializer;

import it.spaghettisource.broadcastsync.exception.ExceptionFactory;

public abstract class SeralizerAbstract<T> implements Serializer<T> {

	protected ExceptionFactory exceptionFactory;

	public SeralizerAbstract(ExceptionFactory exceptionFactory) {
		super();
		this.exceptionFactory = exceptionFactory;
	}
	
}
