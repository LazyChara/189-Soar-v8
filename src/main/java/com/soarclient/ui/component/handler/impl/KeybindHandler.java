package com.soarclient.ui.component.handler.impl;

import com.soarclient.ui.component.handler.ComponentHandler;

public abstract class KeybindHandler extends ComponentHandler {
	public abstract void onBinded(int keyCode);
}