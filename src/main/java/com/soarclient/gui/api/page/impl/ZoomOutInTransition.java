package com.soarclient.gui.api.page.impl;

import com.soarclient.animation.Animation;
import com.soarclient.gui.api.GuiTransition;

public class ZoomOutInTransition extends GuiTransition {

	public ZoomOutInTransition(boolean consecutive) {
		super(consecutive);
	}

	@Override
	public float[] onTransition(Animation animation) {
		return new float[] { 0, 0, 2 - animation.getValue() };
	}

}
