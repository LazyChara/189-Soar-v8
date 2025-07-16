package com.soarclient.management.mod.impl.hud;

import com.soarclient.event.EventBus;
import com.soarclient.event.impl.AttackEntityEventListener;
import com.soarclient.event.impl.ClientTickEventListener;
import com.soarclient.event.impl.DamageEntityEventListener;
import com.soarclient.event.impl.RenderSkiaEventListener;
import com.soarclient.management.mod.api.hud.SimpleHUDMod;
import com.soarclient.skia.font.Icon;

import net.minecraft.entity.Entity;

public class ComboCounterMod extends SimpleHUDMod implements ClientTickEventListener, AttackEntityEventListener,
		DamageEntityEventListener, RenderSkiaEventListener {

	private long hitTime = -1;
	private int combo, possibleTarget;

	public ComboCounterMod() {
		super("mod.combocounter.name", "mod.combocounter.description", Icon.PERSON_ADD);
	}

	@Override
	public void onRenderSkia(float partialTicks) {
		super.draw();
	}

	@Override
	public void onDamageEntity(Entity entity) {
		if (entity.getEntityId() == possibleTarget) {
			combo++;
			possibleTarget = -1;
			hitTime = System.currentTimeMillis();
		} else if (entity == mc.thePlayer) {
			combo = 0;
		}
	}

	@Override
	public void onAttackEntity(Entity entity) {
		possibleTarget = entity.getEntityId();
	}

	@Override
	public void onClientTick() {
		if ((System.currentTimeMillis() - hitTime) > 2000) {
			combo = 0;
		}
	}

	@Override
	public void onEnable() {
		EventBus.getInstance().registers(this, DamageEntityEvent.ID, AttackEntityEvent.ID, ClientTickEvent.ID,
				RenderSkiaEvent.ID);
	}

	@Override
	public void onDisable() {
		EventBus.getInstance().unregisters(this, DamageEntityEvent.ID, AttackEntityEvent.ID, ClientTickEvent.ID,
				RenderSkiaEvent.ID);
	}

	@Override
	public String getText() {
		if (combo == 0) {
			return "No Combo";
		} else {
			return combo + " Combo";
		}
	}

	@Override
	public String getIcon() {
		return Icon.PERSON_ADD;
	}
}