package com.soarclient.event.impl;

import com.soarclient.event.api.CancellableEvent;

import net.minecraft.network.Packet;

public interface ReceivePacketEventListener {

	void onReceivePacket(Packet<?> packet);

	class ReceivePacketEvent extends CancellableEvent<ReceivePacketEventListener> {

		public static final int ID = 8;
		private final Packet<?> packet;

		public ReceivePacketEvent(Packet<?> packet) {
			this.packet = packet;
		}

		@Override
		public void call(ReceivePacketEventListener listener) {
			listener.onReceivePacket(packet);
		}
	}
}
