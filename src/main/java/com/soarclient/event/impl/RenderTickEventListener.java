package com.soarclient.event.impl;

import com.soarclient.event.api.AbstractEvent;

public interface RenderTickEventListener {

	void onRenderTick();

	class RenderTickEvent extends AbstractEvent<RenderTickEventListener> {

		public static final int ID = 1;

		@Override
		public void call(RenderTickEventListener listener) {
			listener.onRenderTick();
		}
	}
}
