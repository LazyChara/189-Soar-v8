package com.soarclient.management.mod.settings.impl;

import com.soarclient.Soar;
import com.soarclient.management.mod.Mod;
import com.soarclient.management.mod.settings.Setting;

public class KeybindSetting extends Setting {

	private int defaultKeyCode, keyCode;
	private int pressTime;
	private boolean pressed;

	public KeybindSetting(String name, String description, String icon, Mod parent, int keyCode) {
		super(name, description, icon, parent);

		this.defaultKeyCode = keyCode;
		this.keyCode = keyCode;

		Soar.getInstance().getModManager().addSetting(this);
	}

	@Override
	public void reset() {
		this.keyCode = this.defaultKeyCode;
	}

	public int getKeyCode() {
		return keyCode;
	}

	public void setKeyCode(int keyCode) {
		this.keyCode = keyCode;
	}

	public int getDefaultKeyCode() {
		return defaultKeyCode;
	}

	public void setKeybindState(boolean state) {
		pressed = state;
	}

	public void onTick() {
		pressTime++;
	}

	public boolean isKeyDown() {
		return pressed;
	}

	public boolean isPressed() {
		if (this.pressTime == 0) {
			return false;
		} else {
			this.pressTime--;
			return true;
		}
	}

	public void unPress() {
		pressed = false;
	}
}