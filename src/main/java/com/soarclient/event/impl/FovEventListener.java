package com.soarclient.event.impl;

import com.soarclient.event.api.AbstractEvent;

import net.minecraft.client.entity.AbstractClientPlayer;

public interface FovEventListener {

	void onFovUpdate(FovUpdateEvent event);

	class FovUpdateEvent extends AbstractEvent<FovEventListener> {

		public static final int ID = 9;

		private final AbstractClientPlayer entity;
		private float fov;

		public FovUpdateEvent(AbstractClientPlayer entity, float fov) {
			this.entity = entity;
			this.fov = fov;
		}

		@Override
		public void call(FovEventListener listener) {
			listener.onFovUpdate(this);
		}

		public float getFov() {
			return fov;
		}

		public void setFov(float fov) {
			this.fov = fov;
		}

		public AbstractClientPlayer getEntity() {
			return entity;
		}
	}
}
