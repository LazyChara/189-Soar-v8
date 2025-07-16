package com.soarclient.event.impl;

import com.soarclient.event.api.AbstractEvent;

public interface MotionUpdateEventListener {

	void onMotionUpdate();

	class MotionUpdateEvent extends AbstractEvent<MotionUpdateEventListener> {

		public static final int ID = 12;

		@Override
		public void call(MotionUpdateEventListener listener) {
			listener.onMotionUpdate();
		}
	}
}
