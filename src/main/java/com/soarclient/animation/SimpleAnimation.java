package com.soarclient.animation;

public class SimpleAnimation {

	private float currentValue;
	private boolean firstTick;

	public SimpleAnimation() {
		this.firstTick = true;
	}

	public void onTick(float value, float speed) {
		if (firstTick) {
			currentValue = value;
			firstTick = false;
		} else {
			float delta = (float) (((value - currentValue) * (speed / 1000)) * Delta.getDeltaTime());
			currentValue += delta;
		}
	}

	public float getValue() {
		return currentValue;
	}

	public void setFirstTick(boolean firstTick) {
		this.firstTick = firstTick;
	}
}