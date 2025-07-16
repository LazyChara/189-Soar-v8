package com.soarclient.event.impl;

import com.soarclient.event.api.AbstractEvent;

public interface HurtCameraEventListener {

	void onHurtCamera(HurtCameraEvent event);

	class HurtCameraEvent extends AbstractEvent<HurtCameraEventListener> {

		public static final int ID = 19;
		private float intensity;

		public HurtCameraEvent() {
			this.intensity = 1.0F;
		}

		@Override
		public void call(HurtCameraEventListener listener) {
			listener.onHurtCamera(this);
		}

		public float getIntensity() {
			return intensity;
		}

		public void setIntensity(float intensity) {
			this.intensity = intensity;
		}
	}
}
