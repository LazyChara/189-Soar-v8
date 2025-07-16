package com.soarclient.event.impl;

import com.soarclient.event.api.AbstractEvent;

public interface ClientTickEventListener {

	void onClientTick();

	class ClientTickEvent extends AbstractEvent<ClientTickEventListener> {

		public static final int ID = 0;

		@Override
		public void call(ClientTickEventListener listener) {
			listener.onClientTick();
		}
	}
}
