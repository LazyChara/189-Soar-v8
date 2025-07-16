package com.soarclient.event.impl;

import com.soarclient.event.api.CancellableEvent;

public interface MouseClickEventListener {

	void onMouseClick(int button);

	class MouseClickEvent extends CancellableEvent<MouseClickEventListener> {

		public static final int ID = 20;
		private final int button;

		public MouseClickEvent(int button) {
			this.button = button;
		}

		@Override
		public void call(MouseClickEventListener listener) {
			listener.onMouseClick(button);
		}
	}
}
