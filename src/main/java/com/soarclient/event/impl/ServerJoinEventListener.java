package com.soarclient.event.impl;

import com.soarclient.event.api.AbstractEvent;

public interface ServerJoinEventListener {

	void onJoinServer();

	class JoinServerEvent extends AbstractEvent<ServerJoinEventListener> {

		public static final int ID = 17;

		@Override
		public void call(ServerJoinEventListener listener) {
			listener.onJoinServer();
		}
	}
}
