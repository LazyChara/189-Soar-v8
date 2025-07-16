package com.soarclient.ui.component.api;

import java.awt.Color;

import com.soarclient.animation.Animation;
import com.soarclient.animation.Duration;
import com.soarclient.animation.cubicbezier.impl.EaseEmphasizedDecelerate;
import com.soarclient.animation.other.DummyAnimation;
import com.soarclient.skia.Skia;
import com.soarclient.utils.ColorUtils;
import com.soarclient.utils.MathUtils;

public class PressAnimation {

	private Animation animation;

	public PressAnimation() {
		animation = new DummyAnimation();
	}

	public void setPressed() {
		animation = new DummyAnimation(1);
	}

	public void draw(float x, float y, float width, float height, Color color, float alpha) {

		float radius = calculateMaxRadius(width, height) * 1.1F;

		float resultAlpha = alpha;

		if (animation.getEnd() == 1) {
			resultAlpha = animation.getValue() * resultAlpha;
		} else if (animation.getEnd() == 2) {
			resultAlpha = (2 - animation.getValue()) * resultAlpha;
		} else {
			resultAlpha = 0;
		}

		if (animation.getEnd() == 2 && animation.isFinished()) {
			animation = new DummyAnimation();
		}

		Skia.drawCircle(x, y, radius * MathUtils.clamp(animation.getValue(), 0, 1),
				ColorUtils.applyAlpha(color, resultAlpha));
	}

	public void mousePressed() {
		animation = new EaseEmphasizedDecelerate(Duration.EXTRA_LONG_2, 0, 1);
	}

	public void mouseReleased() {
		if (animation.getEnd() == 1) {
			animation = new EaseEmphasizedDecelerate(Duration.EXTRA_LONG_2, animation.getValue(), 2);
		}
	}

	private float calculateMaxRadius(float width, float height) {
		return (float) Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));
	}
}
