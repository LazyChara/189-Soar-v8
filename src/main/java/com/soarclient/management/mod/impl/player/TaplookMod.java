package com.soarclient.management.mod.impl.player;

import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import com.soarclient.event.EventBus;
import com.soarclient.event.impl.ClientTickEventListener;
import com.soarclient.management.mod.Mod;
import com.soarclient.management.mod.ModCategory;
import com.soarclient.management.mod.settings.impl.BooleanSetting;
import com.soarclient.management.mod.settings.impl.ComboSetting;
import com.soarclient.management.mod.settings.impl.KeybindSetting;
import com.soarclient.skia.font.Icon;

public class TaplookMod extends Mod implements ClientTickEventListener {

	private boolean active;
	private int prevPerspective;
	private boolean toggled;

	private ComboSetting perspectiveSetting = new ComboSetting("setting.perspective", "setting.perspective.description",
			Icon.CAMERASWITCH, this, Arrays.asList("setting.front", "setting.behind"), "setting.front");
	private BooleanSetting toggleSetting = new BooleanSetting("setting.toggle", "setting.toggle.description",
			Icon.SWITCH, this, false);
	private KeybindSetting keybindSetting = new KeybindSetting("setting.keybind", "setting.keybind.description",
			Icon.KEYBOARD, this, Keyboard.KEY_V);

	public TaplookMod() {
		super("mod.taplook.name", "mod.taplook.description", Icon.TOUCH_APP, ModCategory.PLAYER);
	}

	@Override
	public void onClientTick() {

		if (toggleSetting.isEnabled()) {

			if (keybindSetting.isPressed()) {
				toggled = !toggled;

				if (toggled) {
					if (!active) {
						this.start();
					}
				} else {
					if (active) {
						this.stop();
					}
				}
			}
		} else {
			if (keybindSetting.isKeyDown()) {
				if (!active) {
					this.start();
				}
			} else if (active) {
				this.stop();
			}
		}
	}

	@Override
	public void onEnable() {
		EventBus.getInstance().register(this, ClientTickEvent.ID);
	}

	@Override
	public void onDisable() {
		EventBus.getInstance().unregister(this, ClientTickEvent.ID);
	}

	private void start() {

		String option = perspectiveSetting.getOption();
		int perspective = option.equals("setting.front") ? 2 : 1;

		active = true;
		prevPerspective = mc.gameSettings.thirdPersonView;
		mc.gameSettings.thirdPersonView = perspective;
		mc.renderGlobal.setDisplayListEntitiesDirty();
	}

	private void stop() {
		active = false;
		mc.gameSettings.thirdPersonView = prevPerspective;
		mc.renderGlobal.setDisplayListEntitiesDirty();
	}
}
