package com.soarclient.event.impl;

import com.soarclient.event.api.CancellableEvent;

public interface RenderPumpkinOverlayEventListener {

	void onRenderPumpkinOverlay(RenderPumpkinOverlayEvent event);

	class RenderPumpkinOverlayEvent extends CancellableEvent<RenderPumpkinOverlayEventListener> {

		public static final int ID = 5;

		@Override
		public void call(RenderPumpkinOverlayEventListener listener) {
			listener.onRenderPumpkinOverlay(this);
		}
	}
}
