package com.soarclient.management.mod.impl.hud;

import java.text.DecimalFormat;

import com.soarclient.event.EventBus;
import com.soarclient.event.impl.DamageEntityEventListener;
import com.soarclient.event.impl.RenderSkiaEventListener;
import com.soarclient.management.mod.api.hud.SimpleHUDMod;
import com.soarclient.skia.font.Icon;

import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;

public class ReachDisplayMod extends SimpleHUDMod implements RenderSkiaEventListener, DamageEntityEventListener {

	private DecimalFormat df = new DecimalFormat("0.##");

	private double distance = 0;
	private long hitTime = -1;

	public ReachDisplayMod() {
		super("mod.reachdisplay.name", "mod.reachdisplay.description", Icon.SOCIAL_DISTANCE);
	}

	@Override
	public void onRenderSkia(float partialTicks) {
		super.draw();
	}

	@Override
	public void onDamageEntity(Entity entity) {
		if (mc.objectMouseOver != null && mc.objectMouseOver.hitVec != null
				&& mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
			distance = mc.objectMouseOver.hitVec.distanceTo(mc.thePlayer.getPositionEyes(1.0F));
			hitTime = System.currentTimeMillis();
		}
	}

	@Override
	public void onEnable() {
		EventBus.getInstance().registers(this, RenderSkiaEvent.ID, DamageEntityEvent.ID);
	}

	@Override
	public void onDisable() {
		EventBus.getInstance().unregisters(this, RenderSkiaEvent.ID, DamageEntityEvent.ID);
	}

	@Override
	public String getText() {

		if ((System.currentTimeMillis() - hitTime) > 5000) {
			distance = 0;
		}

		if (distance == 0) {
			return "Hasn't attacked";
		} else {
			return df.format(distance) + " blocks";
		}
	}

	@Override
	public String getIcon() {
		return Icon.SOCIAL_DISTANCE;
	}
}