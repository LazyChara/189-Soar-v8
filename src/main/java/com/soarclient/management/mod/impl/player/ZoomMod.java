
package com.soarclient.management.mod.impl.player;

import org.lwjgl.input.Keyboard;

import com.soarclient.animation.SimpleAnimation;
import com.soarclient.event.EventBus;
import com.soarclient.event.impl.ClientTickEventListener;
import com.soarclient.event.impl.MouseScrollEventListener;
import com.soarclient.management.mod.Mod;
import com.soarclient.management.mod.ModCategory;
import com.soarclient.management.mod.settings.impl.BooleanSetting;
import com.soarclient.management.mod.settings.impl.KeybindSetting;
import com.soarclient.management.mod.settings.impl.NumberSetting;
import com.soarclient.skia.font.Icon;

public class ZoomMod extends Mod implements MouseScrollEventListener, ClientTickEventListener {

	private static ZoomMod instance;

	private SimpleAnimation animation = new SimpleAnimation();

	private boolean active;
	private float lastSensitivity;
	private float currentFactor = 1;

	public boolean wasCinematic;

	private BooleanSetting scrollSetting = new BooleanSetting("setting.scroll", "setting.zoom.scroll.description",
			Icon.SCROLLABLE_HEADER, this, false);
	private BooleanSetting smoothZoomSetting = new BooleanSetting("setting.smoothzoom",
			"setting.smoothzoom.description", Icon.MOTION_BLUR, this, false);
	private NumberSetting zoomSpeedSetting = new NumberSetting("setting.zoomspeed", "setting.zoomspeed.description",
			Icon.SPEED, this, 0.4F, 0, 1, 0.1F);
	private NumberSetting factorSetting = new NumberSetting("setting.zoomfactor", "setting.zoomfactor.description",
			Icon.ZOOM_OUT, this, 4, 2, 15, 1);
	private BooleanSetting smoothCameraSetting = new BooleanSetting("setting.smoothcamera",
			"setting.smoothcamera.description", Icon.MOTION_BLUR, this, true);
	private KeybindSetting keybindSetting = new KeybindSetting("setting.keybind", "setting.keybind.description",
			Icon.KEYBOARD, this, Keyboard.KEY_C);

	public ZoomMod() {
		super("mod.zoom.name", "mod.zoom.description", Icon.ZOOM_IN, ModCategory.PLAYER);

		instance = this;
	}

	@Override
	public void onMouseScroll(MouseScrollEvent event) {
		if (active && scrollSetting.isEnabled()) {
			event.setCancelled(true);
			if (event.getAmount() < 0) {
				if (currentFactor < 0.98) {
					currentFactor += 0.03;
				}
			} else if (event.getAmount() > 0) {
				if (currentFactor > 0.06) {
					currentFactor -= 0.03;
				}
			}
		}
	}

	@Override
	public void onClientTick() {
		if (keybindSetting.isKeyDown()) {
			if (!active) {
				active = true;
				lastSensitivity = mc.gameSettings.mouseSensitivity;
				resetFactor();
				wasCinematic = this.mc.gameSettings.smoothCamera;
				mc.gameSettings.smoothCamera = smoothCameraSetting.isEnabled();
				mc.renderGlobal.setDisplayListEntitiesDirty();
			}
		} else if (active) {
			active = false;
			setFactor(1);
			mc.gameSettings.mouseSensitivity = lastSensitivity;
			mc.gameSettings.smoothCamera = wasCinematic;
		}
	}

	public void resetFactor() {
		setFactor(1 / factorSetting.getValue());
	}

	public void setFactor(float factor) {
		if (factor != currentFactor) {
			mc.renderGlobal.setDisplayListEntitiesDirty();
		}
		currentFactor = factor;
	}

	public float getFov(float fov) {

		if (smoothZoomSetting.isEnabled()) {
			animation.onTick(currentFactor, zoomSpeedSetting.getValue() * 10);
		}

		return fov * (smoothZoomSetting.isEnabled() ? animation.getValue() : currentFactor);
	}

	@Override
	public void onEnable() {
		EventBus.getInstance().registers(this, ClientTickEvent.ID, MouseScrollEvent.ID);
	}

	@Override
	public void onDisable() {
		EventBus.getInstance().unregisters(this, ClientTickEvent.ID, MouseScrollEvent.ID);
	}

	public static ZoomMod getInstance() {
		return instance;
	}
}
