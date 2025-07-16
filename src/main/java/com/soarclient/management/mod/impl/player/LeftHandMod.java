package com.soarclient.management.mod.impl.player;

import com.soarclient.management.mod.Mod;
import com.soarclient.management.mod.ModCategory;
import com.soarclient.skia.font.Icon;

public class LeftHandMod extends Mod {

	private static LeftHandMod instance;
	private boolean isRenderingItemInFirstPerson;

	public LeftHandMod() {
		super("mod.lefthand.name", "mod.lefthand.description", Icon.BACK_HAND, ModCategory.PLAYER);

		instance = this;
	}

	public static LeftHandMod getInstance() {
		return instance;
	}

	public boolean isRenderingItemInFirstPerson() {
		return isRenderingItemInFirstPerson;
	}

	public void setRenderingItemInFirstPerson(boolean isRenderingItemInFirstPerson) {
		this.isRenderingItemInFirstPerson = isRenderingItemInFirstPerson;
	}

	@Override
	public void onEnable() {
	}

	@Override
	public void onDisable() {
	}
}