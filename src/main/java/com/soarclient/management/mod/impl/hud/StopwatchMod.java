package com.soarclient.management.mod.impl.hud;

import java.text.DecimalFormat;

import org.lwjgl.input.Keyboard;

import com.soarclient.event.EventBus;
import com.soarclient.event.impl.ClientTickEventListener;
import com.soarclient.event.impl.RenderSkiaEventListener;
import com.soarclient.management.mod.api.hud.SimpleHUDMod;
import com.soarclient.management.mod.settings.impl.KeybindSetting;
import com.soarclient.skia.font.Icon;
import com.soarclient.utils.TimerUtils;

public class StopwatchMod extends SimpleHUDMod implements RenderSkiaEventListener, ClientTickEventListener {

	private TimerUtils timer = new TimerUtils();
	private int pressCount;
	private float currentTime;
	private DecimalFormat timeFormat = new DecimalFormat("0.00");

	private KeybindSetting keybindSetting = new KeybindSetting("setting.keybind", "setting.keybind.description",
			Icon.KEYBOARD, this, Keyboard.KEY_P);

	public StopwatchMod() {
		super("mod.stopwatch.name", "mod.stopwatch.description", Icon.TIMER);
	}

	@Override
	public void onRenderSkia(float partialTicks) {
		super.draw();
	}

	@Override
	public void onClientTick() {
		if (keybindSetting.isPressed()) {
			pressCount++;
		}

		switch (pressCount) {
		case 0:
			timer.reset();
			break;
		case 1:
			currentTime = (timer.getElapsedTime() / 1000F);
			break;
		case 3:
			timer.reset();
			currentTime = 0;
			pressCount = 0;
			break;
		}
	}

	@Override
	public void onEnable() {

		EventBus.getInstance().registers(this, RenderSkiaEvent.ID, ClientTickEvent.ID);

		if (timer != null) {
			timer.reset();
		}

		pressCount = 0;
		currentTime = 0;
	}

	@Override
	public void onDisable() {
		EventBus.getInstance().unregisters(this, RenderSkiaEvent.ID, ClientTickEvent.ID);
	}

	@Override
	public String getText() {
		return timeFormat.format(currentTime) + " s";
	}

	@Override
	public String getIcon() {
		return Icon.TIMER;
	}
}