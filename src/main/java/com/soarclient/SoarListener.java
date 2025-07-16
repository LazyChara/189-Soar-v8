package com.soarclient;

import com.soarclient.event.impl.ClientTickEventListener;

public class SoarListener implements ClientTickEventListener {

	@Override
	public void onClientTick() {
		Soar.getInstance().getColorManager().onTick();
	}
}
