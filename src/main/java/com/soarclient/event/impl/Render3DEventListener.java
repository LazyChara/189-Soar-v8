package com.soarclient.event.impl;

import com.soarclient.event.api.AbstractEvent;

public interface Render3DEventListener {

	void onRender3D(float partialTicks);

	class Render3DEvent extends AbstractEvent<Render3DEventListener> {

		public static final int ID = 24;
		private float partialTicks;

		public Render3DEvent(float partialTicks) {
			this.partialTicks = partialTicks;
		}

		@Override
		public void call(Render3DEventListener listener) {
			listener.onRender3D(partialTicks);
		}
	}
}
