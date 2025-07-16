package com.soarclient.management.mod.impl.hud;

import java.awt.Color;

import com.soarclient.event.EventBus;
import com.soarclient.event.impl.ClientTickEventListener;
import com.soarclient.event.impl.RenderSkiaEventListener;
import com.soarclient.management.mod.api.hud.HUDMod;
import com.soarclient.management.mod.impl.hud.minimap.MapCapture;
import com.soarclient.management.mod.settings.impl.NumberSetting;
import com.soarclient.skia.Skia;
import com.soarclient.skia.font.Icon;
import com.soarclient.utils.ColorUtils;

import net.minecraft.entity.player.EntityPlayer;

public class MinimapMod extends HUDMod implements RenderSkiaEventListener, ClientTickEventListener {

	private NumberSetting widthSetting = new NumberSetting("setting.width", "setting.width.description", Icon.WIDTH,
			this, 150, 10, 180, 1);
	private NumberSetting heightSetting = new NumberSetting("setting.height", "setting.height.description", Icon.HEIGHT,
			this, 75, 10, 180, 1);
	private NumberSetting alphaSetting = new NumberSetting("setting.alpha", "setting.alpha.description", Icon.OPACITY,
			this, 1F, 0.0F, 1F, 0.1F);

	private MapCapture mapCapture = new MapCapture();

	public MinimapMod() {
		super("mod.minimap.name", "mod.minimap.description", Icon.MAP);
	}

	@Override
	public void onRenderSkia(float partialTicks) {

		float width = widthSetting.getValue();
		float height = heightSetting.getValue();

		EntityPlayer p = mc.thePlayer;
		float yaw = (float) lerp(p.prevRotationYaw, p.rotationYaw, partialTicks);

		this.begin();
		this.drawBackground(getX(), getY(), width, height);
		mc.getTextureManager()
				.bindTexture(mc.getTextureManager().getDynamicTextureLocation("minimap", mapCapture.getTexture()));
		Skia.clip(getX(), getY(), width, height, getRadius());
		Skia.drawRect(getX(), getY(), width, height,
				ColorUtils.applyAlpha(new Color(138, 176, 254), alphaSetting.getValue()));
		Skia.scale(getX(), getY(), 128, 128, 1.7F);
		Skia.rotate(getX(), getY(), 128, 128, 180 - yaw);
		Skia.drawImage(mapCapture.getTexture().getGlTextureId(), getX(), getY(), 128, 128, alphaSetting.getValue());
		this.finish();

		position.setSize(width, height);
	}

	@Override
	public void onClientTick() {
		if (mc.theWorld != null && mc.thePlayer != null) {
			mapCapture.update(mc.theWorld);
		}
	}

	@Override
	public void onEnable() {
		EventBus.getInstance().registers(this, RenderSkiaEvent.ID, ClientTickEvent.ID);
	}

	@Override
	public void onDisable() {
		EventBus.getInstance().unregisters(this, RenderSkiaEvent.ID, ClientTickEvent.ID);
	}

	@Override
	public float getRadius() {
		return 10;
	}

	private double lerp(double prev, double current, float partialTicks) {
		return prev + (current - prev) * partialTicks;
	}
}
