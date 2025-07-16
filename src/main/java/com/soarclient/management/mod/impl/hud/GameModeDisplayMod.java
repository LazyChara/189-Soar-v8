package com.soarclient.management.mod.impl.hud;

import com.soarclient.event.EventBus;
import com.soarclient.event.impl.RenderSkiaEventListener;
import com.soarclient.management.mod.api.hud.SimpleHUDMod;
import com.soarclient.skia.font.Icon;
import com.soarclient.utils.PlayerUtils;

public class GameModeDisplayMod extends SimpleHUDMod implements RenderSkiaEventListener {

	public GameModeDisplayMod() {
		super("mod.gamemodedisplay.name", "mod.gamemodedisplay.description", Icon.SPORTS_ESPORTS);
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

		String prefix = "Mode: ";

		if (PlayerUtils.isSurvival()) {
			return prefix + "Survival";
		}

		if (PlayerUtils.isCreative()) {
			return prefix + "Creative";
		}

		if (PlayerUtils.isSpectator()) {
			return prefix + "Spectator";
		}

		return prefix + "Error";
	}

	@Override
	public String getIcon() {
		return Icon.SPORTS_ESPORTS;
	}
}