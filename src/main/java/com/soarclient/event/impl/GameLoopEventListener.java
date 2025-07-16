package com.soarclient.event.impl;

import com.soarclient.event.api.AbstractEvent;

public interface GameLoopEventListener {

	void onGameLoop();

	class GameLoopEvent extends AbstractEvent<GameLoopEventListener> {

		public static final int ID = 16;

		@Override
		public void call(GameLoopEventListener listener) {
			listener.onGameLoop();
		}
	}
}
