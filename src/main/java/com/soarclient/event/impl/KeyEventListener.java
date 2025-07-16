package com.soarclient.event.impl;

import com.soarclient.event.api.CancellableEvent;

public interface KeyEventListener {

	void onKey(int keyCode);

	class KeyEvent extends CancellableEvent<KeyEventListener> {

		public static final int ID = 23;

		private int keyCode;

		public KeyEvent(int keyCode) {
			this.keyCode = keyCode;
		}

		@Override
		public void call(KeyEventListener listener) {
			listener.onKey(keyCode);
		}
	}
}
