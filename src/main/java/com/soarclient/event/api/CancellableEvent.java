package com.soarclient.event.api;

public abstract class CancellableEvent<T> extends AbstractEvent<T> {

	private boolean cancelled;

	public void cancel() {
		setCancelled(true);
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
