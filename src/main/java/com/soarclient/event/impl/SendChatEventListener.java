package com.soarclient.event.impl;

import com.soarclient.event.api.CancellableEvent;

public interface SendChatEventListener {

	void onSendChat(String message);

	class SendChatEvent extends CancellableEvent<SendChatEventListener> {

		public static final int ID = 11;
		private final String message;

		public SendChatEvent(String message) {
			this.message = message;
		}

		@Override
		public void call(SendChatEventListener listener) {
			listener.onSendChat(message);
		}
	}
}
