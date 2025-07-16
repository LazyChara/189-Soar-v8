package com.soarclient.management.mod.impl.hud;

import com.soarclient.event.EventBus;
import com.soarclient.event.impl.HurtCameraEventListener;
import com.soarclient.event.impl.RenderFireOverlayEventListener;
import com.soarclient.event.impl.RenderPumpkinOverlayEventListener;
import com.soarclient.event.impl.RenderSkiaEventListener;
import com.soarclient.event.impl.RenderTickEventListener;
import com.soarclient.event.impl.RenderWaterOverlayEventListener;
import com.soarclient.management.mod.api.hud.HUDMod;
import com.soarclient.management.mod.impl.hud.rearview.RearviewCamera;
import com.soarclient.management.mod.settings.impl.NumberSetting;
import com.soarclient.skia.Skia;
import com.soarclient.skia.font.Icon;
import com.soarclient.utils.TimerUtils;

import io.github.humbleui.skija.SurfaceOrigin;

public class RearviewMod extends HUDMod
		implements RenderTickEventListener, RenderSkiaEventListener, RenderFireOverlayEventListener,
		RenderWaterOverlayEventListener, RenderPumpkinOverlayEventListener, HurtCameraEventListener {

	private static RearviewMod instance;

	private NumberSetting widthSetting = new NumberSetting("setting.width", "setting.width.description", Icon.WIDTH,
			this, 150, 10, 180, 1);
	private NumberSetting heightSetting = new NumberSetting("setting.height", "setting.height.description", Icon.HEIGHT,
			this, 75, 10, 180, 1);
	private NumberSetting alphaSetting = new NumberSetting("setting.alpha", "setting.alpha.description", Icon.OPACITY,
			this, 1F, 0.0F, 1F, 0.1F);
	private NumberSetting fpsSetting = new NumberSetting("setting.fps", "setting.fps.description", Icon._60FPS_SELECT,
			this, 60, 0.0F, 480, 5);

	private RearviewCamera camera = new RearviewCamera();
	private TimerUtils timer = new TimerUtils();

	public RearviewMod() {
		super("mod.rearview.name", "mod.rearview.description", Icon.PHOTO_CAMERA_BACK);

		instance = this;
	}

	@Override
	public void onRenderTick() {

		if (mc.thePlayer != null && mc.theWorld != null) {
			if (timer.delay((long) (1000 / fpsSetting.getValue()))) {
				camera.updateMirror();
				timer.reset();
			}
		}
	}

	@Override
	public void onRenderSkia(float partialTicks) {

		this.begin();
		this.drawBackground(getX(), getY(), widthSetting.getValue(), heightSetting.getValue());
		Skia.drawRoundedImage(camera.getTexture(), getX(), getY(), widthSetting.getValue(), heightSetting.getValue(),
				getRadius(), alphaSetting.getValue(), SurfaceOrigin.BOTTOM_LEFT);
		this.finish();

		position.setSize(widthSetting.getValue(), heightSetting.getValue());
	}

	@Override
	public void onRenderFireOverlay(RenderFireOverlayEvent event) {
		if (camera.isRecording()) {
			event.setCancelled(true);
		}
	}

	@Override
	public void onRenderWaterOverlay(RenderWaterOverlayEvent event) {
		if (camera.isRecording()) {
			event.setCancelled(true);
		}
	}

	@Override
	public void onRenderPumpkinOverlay(RenderPumpkinOverlayEvent event) {
		if (camera.isRecording()) {
			event.setCancelled(true);
		}
	}

	@Override
	public void onHurtCamera(HurtCameraEvent event) {
		if (camera.isRecording()) {
			event.setIntensity(0);
		}
	}

	@Override
	public void onEnable() {
		EventBus.getInstance().registers(this, RenderTickEvent.ID, RenderSkiaEvent.ID, RenderFireOverlayEvent.ID,
				RenderWaterOverlayEvent.ID, RenderPumpkinOverlayEvent.ID, HurtCameraEvent.ID);
	}

	@Override
	public void onDisable() {
		EventBus.getInstance().unregisters(this, RenderTickEvent.ID, RenderSkiaEvent.ID, RenderFireOverlayEvent.ID,
				RenderWaterOverlayEvent.ID, RenderPumpkinOverlayEvent.ID, HurtCameraEvent.ID);
	}

	@Override
	public float getRadius() {
		return 6;
	}

	public static RearviewMod getInstance() {
		return instance;
	}

	public RearviewCamera getCamera() {
		return camera;
	}
}
