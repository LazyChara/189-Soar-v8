package com.soarclient.management.mod.impl.player;

import com.soarclient.event.EventBus;
import com.soarclient.event.impl.KeyEventListener;
import com.soarclient.event.impl.PlayerUpdateEventListener;
import com.soarclient.management.mod.Mod;
import com.soarclient.management.mod.ModCategory;
import com.soarclient.skia.font.Icon;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.settings.KeyBinding;

public class ToggleSneakMod extends Mod implements PlayerUpdateEventListener, KeyEventListener {

	private boolean toggle;

	public ToggleSneakMod() {
		super("mod.togglesneak.name", "mod.togglesneak.description", Icon.AIRLINE_SEAT_RECLINE_EXTRA,
				ModCategory.PLAYER);

		toggle = false;
	}

	@Override
	public void onKey(int keyCode) {
		if (keyCode == mc.gameSettings.keyBindSneak.getKeyCode()) {
			toggle = !toggle;
		}
	}

	@Override
	public void onUpdate() {
		if (mc.currentScreen instanceof Gui) {
			setSneak(false);
		} else {
			setSneak(toggle);
		}
	}

	@Override
	public void onEnable() {
		EventBus.getInstance().registers(this, PlayerUpdateEvent.ID, KeyEvent.ID);
	}

	@Override
	public void onDisable() {
		EventBus.getInstance().unregisters(this, PlayerUpdateEvent.ID, KeyEvent.ID);
		toggle = false;
		setSneak(false);
	}

	private void setSneak(boolean state) {
		KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), state);
	}
}