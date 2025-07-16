package com.soarclient.management.mod.impl.hud;

import com.soarclient.event.EventBus;
import com.soarclient.event.impl.ClientTickEventListener;
import com.soarclient.event.impl.PlayerHeadRotationEventListener;
import com.soarclient.event.impl.RenderSkiaEventListener;
import com.soarclient.management.mod.api.hud.HUDMod;
import com.soarclient.skia.Skia;
import com.soarclient.skia.font.Icon;

import net.minecraft.util.MathHelper;

public class MouseStrokesMod extends HUDMod
		implements ClientTickEventListener, RenderSkiaEventListener, PlayerHeadRotationEventListener {

	private float mouseX, mouseY, lastMouseX, lastMouseY;

	public MouseStrokesMod() {
		super("mod.mousestrokes.name", "mod.mousestrokes.description", Icon.TOUCHPAD_MOUSE);
	}

	@Override
	public void onRenderSkia(float partialTicks) {

		float calculatedMouseX = (lastMouseX + ((mouseX - lastMouseX) * partialTicks));
		float calculatedMouseY = (lastMouseY + ((mouseY - lastMouseY) * partialTicks));

		this.begin();
		this.drawBackground(getX(), getY(), 58, 58);
		Skia.drawRoundedRect(getX() + calculatedMouseX + 28 - 3.5F, getY() + calculatedMouseY + 28 - 3.5F, 9, 9, 9 / 2,
				this.getDesign().getTextColor());
		this.finish();

		position.setSize(58, 58);
	}

	@Override
	public void onClientTick() {
		lastMouseX = mouseX;
		lastMouseY = mouseY;
		mouseX *= 0.75F;
		mouseY *= 0.75F;
	}

	@Override
	public void onPlayerHeadRotation(PlayerHeadRotationEvent event) {
		mouseX += event.getYaw() / 40F;
		mouseY -= event.getPitch() / 40F;
		mouseX = MathHelper.clamp_float(mouseX, -18, 18);
		mouseY = MathHelper.clamp_float(mouseY, -18, 18);
	}

	@Override
	public float getRadius() {
		return 6;
	}

	@Override
	public void onEnable() {
		EventBus.getInstance().registers(this, RenderSkiaEvent.ID, PlayerHeadRotationEvent.ID, ClientTickEvent.ID);
	}

	@Override
	public void onDisable() {
		EventBus.getInstance().unregisters(this, RenderSkiaEvent.ID, PlayerHeadRotationEvent.ID, ClientTickEvent.ID);
	}
}
