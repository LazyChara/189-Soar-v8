package com.soarclient.event.impl;

import com.soarclient.event.api.AbstractEvent;

public interface JumpEventListener {

	void onJump();

	class JumpEvent extends AbstractEvent<JumpEventListener> {

		public static final int ID = 13;

		@Override
		public void call(JumpEventListener listener) {
			listener.onJump();
		}
	}
}
