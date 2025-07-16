package com.soarclient.event.impl;

import com.soarclient.event.api.AbstractEvent;

public interface RenderGameOverlayEventListener {

	void onRenderGameOverlay(float partialTicks);

	class RenderGameOverlayEvent extends AbstractEvent<RenderGameOverlayEventListener> {

		public static final int ID = 2;

		private final float partialTicks;

		public RenderGameOverlayEvent(float partialTicks) {
			this.partialTicks = partialTicks;
		}

		@Override
		public void call(RenderGameOverlayEventListener listener) {
			listener.onRenderGameOverlay(partialTicks);
		}
	}
}
