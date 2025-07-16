package com.soarclient.event.impl;

import com.soarclient.event.api.AbstractEvent;

public interface ServerLeaveEventListener {

	void onLeaveServer();

	class LeaveServerEvent extends AbstractEvent<ServerLeaveEventListener> {

		public static final int ID = 18;

		@Override
		public void call(ServerLeaveEventListener listener) {
			listener.onLeaveServer();
		}
	}
}
