package com.soarclient.management.mod.impl.misc;

import com.soarclient.management.mod.Mod;
import com.soarclient.management.mod.ModCategory;
import com.soarclient.skia.font.Icon;

public class LiquidFixMod extends Mod {

	private static LiquidFixMod instance;

	public LiquidFixMod() {
		super("mod.liquidfix.name", "mod.liquidfix.descriptio", Icon.CLEANING_BUCKET, ModCategory.MISC);

		instance = this;
	}

	public static LiquidFixMod getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
	}

	@Override
	public void onDisable() {
	}
}
