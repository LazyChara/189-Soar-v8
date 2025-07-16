package com.soarclient.gui.api.page.impl;

import com.soarclient.animation.Animation;
import com.soarclient.gui.api.GuiTransition;

public class DownTransition extends GuiTransition {

	public DownTransition(boolean consecutive) {
		super(consecutive);
	}

	@Override
	public float[] onTransition(Animation animation) {

		float progress = animation.getValue();
		float y = 0;

		if (animation.getEnd() == 1) {
			y = -1 + progress;
		} else {
			y = -1 + progress;
		}

		return new float[] { 0, y, 1 };
	}
}
