package com.soarclient.event.impl;

import com.soarclient.event.api.CancellableEvent;

public interface RenderFireOverlayEventListener {

	void onRenderFireOverlay(RenderFireOverlayEvent event);

	class RenderFireOverlayEvent extends CancellableEvent<RenderFireOverlayEventListener> {

		public static final int ID = 4;

		@Override
		public void call(RenderFireOverlayEventListener listener) {
			listener.onRenderFireOverlay(this);
		}
	}
}
