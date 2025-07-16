package com.soarclient.event.impl;

import com.soarclient.event.api.CancellableEvent;

import net.minecraft.network.Packet;

public interface SendPacketEventListener {

	void onSendPacket(Packet<?> packet);

	class SendPacketEvent extends CancellableEvent<SendPacketEventListener> {

		public static final int ID = 7;
		private final Packet<?> packet;

		public SendPacketEvent(Packet<?> packet) {
			this.packet = packet;
		}

		@Override
		public void call(SendPacketEventListener listener) {
			listener.onSendPacket(packet);
		}
	}
}
