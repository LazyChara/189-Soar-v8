package com.soarclient.libraries.soarium.util.task;

public interface CancellationToken {
	boolean isCancelled();

	void setCancelled();
}
