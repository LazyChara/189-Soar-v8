package com.soarclient.event.api;

public abstract class AbstractEvent<T> {
	public abstract void call(final T listener);
}
