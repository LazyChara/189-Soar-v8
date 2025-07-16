package com.soarclient.management.mod.impl.player;

import com.soarclient.management.mod.Mod;
import com.soarclient.management.mod.ModCategory;
import com.soarclient.skia.font.Icon;

public class MouseDelayFixMod extends Mod {

	private static MouseDelayFixMod instance;

	public MouseDelayFixMod() {
		super("mod.mousedelayfix.name", "mod.mousedelayfix.description", Icon.MOUSE, ModCategory.PLAYER);

		instance = this;
	}

	public static MouseDelayFixMod getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
	}

	@Override
	public void onDisable() {
	}
}