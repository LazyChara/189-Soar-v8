package com.soarclient.gui.api.page.impl;

import com.soarclient.animation.Animation;
import com.soarclient.gui.api.GuiTransition;

public class LeftRightTransition extends GuiTransition {

	public LeftRightTransition(boolean consecutive) {
		super(consecutive);
	}

	@Override
	public float[] onTransition(Animation animation) {

		float progress = animation.getValue();
		float x = 0;

		if (animation.getEnd() == 1) {
			x = -1 + progress;
		} else {
			x = -1 + progress;
		}

		return new float[] { x, 0, 1 };
	}
}
