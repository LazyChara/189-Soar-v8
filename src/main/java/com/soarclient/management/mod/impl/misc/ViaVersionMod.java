package com.soarclient.management.mod.impl.misc;

import com.soarclient.management.mod.Mod;
import com.soarclient.management.mod.ModCategory;
import com.soarclient.skia.font.Icon;
import com.soarclient.utils.Multithreading;
import com.soarclient.viasoar.ViaSoar;
import com.soarclient.viasoar.platform.ViaSoarPlatform;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

public class ViaVersionMod extends Mod {

	private static ViaVersionMod instance;
	private boolean loaded;

	public ViaVersionMod() {
		super("mod.viaversion.name", "mod.viaversion.description", Icon.SYNC_ALT, ModCategory.MISC);

		instance = this;
		loaded = false;
	}

	@Override
	public void onEnable() {
		if (!loaded) {
			loaded = true;
			Multithreading.runAsync(() -> {
				ViaSoar.init(ViaSoarPlatform.INSTANCE);
			});
		}
	}

	@Override
	public void onDisable() {
		if (loaded) {
			ViaSoar.getAsyncVersionSlider().setVersion(ProtocolVersion.v1_8);
			ViaSoar.getManager().setTargetVersion(ProtocolVersion.v1_8);
		}
	}

	public static ViaVersionMod getInstance() {
		return instance;
	}

	public boolean isLoaded() {
		return loaded;
	}
}
