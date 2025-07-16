package com.soarclient.ui.component.handler.impl;

import com.soarclient.ui.component.handler.ComponentHandler;

public abstract class SliderHandler extends ComponentHandler {
	public abstract void onPressed(float value);

	public abstract void onReleased(float value);

	public abstract void onValueChanged(float value);
}
