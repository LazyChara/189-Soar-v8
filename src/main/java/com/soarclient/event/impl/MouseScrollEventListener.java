package com.soarclient.event.impl;

import com.soarclient.event.api.CancellableEvent;

public interface MouseScrollEventListener {

	void onMouseScroll(MouseScrollEvent event);

	class MouseScrollEvent extends CancellableEvent<MouseScrollEventListener> {

		public static final int ID = 21;
		private final int amount;

		public MouseScrollEvent(int amount) {
			this.amount = amount;
		}

		@Override
		public void call(MouseScrollEventListener listener) {
			listener.onMouseScroll(this);
		}

		public int getAmount() {
			return amount;
		}
	}
}
