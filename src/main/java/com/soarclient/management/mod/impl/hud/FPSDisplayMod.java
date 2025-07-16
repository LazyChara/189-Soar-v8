package com.soarclient.management.mod.impl.hud;

import com.soarclient.event.EventBus;
import com.soarclient.event.impl.RenderSkiaEventListener;
import com.soarclient.management.mod.api.hud.SimpleHUDMod;
import com.soarclient.skia.font.Icon;

import net.minecraft.client.Minecraft;

public class FPSDisplayMod extends SimpleHUDMod implements RenderSkiaEventListener {

	public FPSDisplayMod() {
		super("mod.fpsdisplay.name", "mod.fpsdisplay.description", Icon.MONITOR);
	}

	@Override
	public void onRenderSkia(float partialTicks) {
		super.draw();
	}

	@Override
	public void onEnable() {
		EventBus.getInstance().register(this, RenderSkiaEvent.ID);
	}

	@Override
	public void onDisable() {
		EventBus.getInstance().unregister(this, RenderSkiaEvent.ID);
	}

	@Override
	public String getText() {
		return Minecraft.getDebugFPS() + " FPS";
	}

	@Override
	public String getIcon() {
		return Icon.MONITOR;
	}
}