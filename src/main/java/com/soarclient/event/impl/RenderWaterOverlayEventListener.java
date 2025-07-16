package com.soarclient.event.impl;

import com.soarclient.event.api.CancellableEvent;

public interface RenderWaterOverlayEventListener {

	void onRenderWaterOverlay(RenderWaterOverlayEvent event);

	class RenderWaterOverlayEvent extends CancellableEvent<RenderWaterOverlayEventListener> {

		public static final int ID = 6;

		@Override
		public void call(RenderWaterOverlayEventListener listener) {
			listener.onRenderWaterOverlay(this);
		}
	}
}
