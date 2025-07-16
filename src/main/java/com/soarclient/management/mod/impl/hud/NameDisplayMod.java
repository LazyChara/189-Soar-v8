package com.soarclient.management.mod.impl.hud;

import com.soarclient.event.EventBus;
import com.soarclient.event.impl.RenderSkiaEventListener;
import com.soarclient.management.mod.api.hud.SimpleHUDMod;
import com.soarclient.skia.font.Icon;

public class NameDisplayMod extends SimpleHUDMod implements RenderSkiaEventListener {

	public NameDisplayMod() {
		super("mod.namedisplay.name", "mod.namedisplay.description", Icon.PERSON);
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
		return "Name: " + mc.thePlayer.getGameProfile().getName();
	}

	@Override
	public String getIcon() {
		return Icon.PERSON;
	}
}