package com.soarclient.management.mod.impl.misc;

import com.soarclient.management.mod.Mod;
import com.soarclient.management.mod.ModCategory;
import com.soarclient.management.mod.settings.impl.StringSetting;
import com.soarclient.skia.font.Icon;

public class NameProtectMod extends Mod {

	private static NameProtectMod instance;
	private StringSetting nameSetting = new StringSetting("setting.name", "setting.name.description", Icon.BADGE, this,
			"You");

	public NameProtectMod() {
		super("mod.nameprotect.name", "mod.nameprotect.description", Icon.HIDE_SOURCE, ModCategory.MISC);

		instance = this;
	}

	public static NameProtectMod getInstance() {
		return instance;
	}

	public StringSetting getNameSetting() {
		return nameSetting;
	}

	@Override
	public void onEnable() {
	}

	@Override
	public void onDisable() {
	}
}