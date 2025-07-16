package com.soarclient.management.mod.impl.hud;

import com.soarclient.event.EventBus;
import com.soarclient.event.impl.RenderSkiaEventListener;
import com.soarclient.management.mod.api.hud.SimpleHUDMod;
import com.soarclient.skia.font.Icon;
import com.soarclient.utils.PlayerUtils;

import net.minecraft.potion.Potion;

public class PotionCounterMod extends SimpleHUDMod implements RenderSkiaEventListener {

	public PotionCounterMod() {
		super("mod.potioncounter.name", "mod.potioncounter.description", Icon.EXPERIMENT);
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

		int amount = PlayerUtils.getPotionsFromInventory(Potion.heal);

		return amount + " " + (amount <= 1 ? "pot" : "pots");
	}

	@Override
	public String getIcon() {
		return Icon.EXPERIMENT;
	}
}