package com.soarclient.event.impl;

import com.soarclient.event.api.AbstractEvent;

public interface PlayerUpdateEventListener {

	void onUpdate();

	class PlayerUpdateEvent extends AbstractEvent<PlayerUpdateEventListener> {

		public static final int ID = 10;

		@Override
		public void call(PlayerUpdateEventListener listener) {
			listener.onUpdate();
		}
	}
}
