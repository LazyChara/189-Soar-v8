package com.soarclient.event.impl;

import com.soarclient.event.api.CancellableEvent;

public interface PlayerHeadRotationEventListener {

	void onPlayerHeadRotation(PlayerHeadRotationEvent event);

	class PlayerHeadRotationEvent extends CancellableEvent<PlayerHeadRotationEventListener> {

		public static final int ID = 25;
		private final float yaw, pitch;

		public PlayerHeadRotationEvent(float yaw, float pitch) {
			this.yaw = yaw;
			this.pitch = pitch;
		}

		@Override
		public void call(PlayerHeadRotationEventListener listener) {
			listener.onPlayerHeadRotation(this);
		}

		public float getYaw() {
			return yaw;
		}

		public float getPitch() {
			return pitch;
		}
	}
}
