package com.soarclient.event.impl;

import com.soarclient.event.api.AbstractEvent;

public interface RenderSkiaEventListener {

	void onRenderSkia(float partialTicks);

	class RenderSkiaEvent extends AbstractEvent<RenderSkiaEventListener> {

		public static final int ID = 3;

		private final float partialTicks;

		public RenderSkiaEvent(float partialTicks) {
			this.partialTicks = partialTicks;
		}

		@Override
		public void call(RenderSkiaEventListener listener) {
			listener.onRenderSkia(partialTicks);
		}
	}
}
