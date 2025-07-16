package com.soarclient.management.mod.impl.hud;

import com.soarclient.event.EventBus;
import com.soarclient.event.impl.RenderSkiaEventListener;
import com.soarclient.management.mod.api.hud.SimpleHUDMod;
import com.soarclient.skia.font.Icon;
import com.soarclient.utils.network.ServerUtils;

public class ServerIPDisplayMod extends SimpleHUDMod implements RenderSkiaEventListener {

	public ServerIPDisplayMod() {
		super("mod.serveripdisplay.name", "mod.serveripdisplay.description", Icon.DNS);
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
		return ServerUtils.getServerIP();
	}

	@Override
	public String getIcon() {
		return Icon.DNS;
	}
}