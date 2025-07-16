package com.soarclient.management.mod.impl.hud;

import com.soarclient.event.EventBus;
import com.soarclient.event.impl.RenderSkiaEventListener;
import com.soarclient.management.mod.api.hud.SimpleHUDMod;
import com.soarclient.skia.font.Icon;

public class HealthDisplayMod extends SimpleHUDMod implements RenderSkiaEventListener {

	public HealthDisplayMod() {
		super("mod.healthdisplay.name", "mod.healthdisplay.description", Icon.FAVORITE);
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
		return (int) mc.thePlayer.getHealth() + " Health";
	}

	@Override
	public String getIcon() {
		return Icon.FAVORITE;
	}
}