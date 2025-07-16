package com.soarclient.management.mod.impl.player;

import org.lwjgl.input.Keyboard;

import com.soarclient.event.EventBus;
import com.soarclient.event.impl.ClientTickEventListener;
import com.soarclient.event.impl.PlayerUpdateEventListener;
import com.soarclient.event.impl.RenderSkiaEventListener;
import com.soarclient.management.mod.ModCategory;
import com.soarclient.management.mod.api.hud.SimpleHUDMod;
import com.soarclient.management.mod.settings.impl.BooleanSetting;
import com.soarclient.skia.font.Icon;

import net.minecraft.client.settings.KeyBinding;

public class ToggleSprintMod extends SimpleHUDMod
		implements RenderSkiaEventListener, PlayerUpdateEventListener, ClientTickEventListener {

	private BooleanSetting hudSetting = new BooleanSetting("setting.hud", "setting.hud.description", Icon.MONITOR, this,
			true);
	private BooleanSetting alwaysSetting = new BooleanSetting("setting.always", "setting.always.description",
			Icon.SPRINT, this, false);

	private long startTime;
	private boolean wasDown;

	private State state;

	public ToggleSprintMod() {
		super("mod.togglesprint.name", "mod.togglesprint.description", Icon.SPRINT);

		this.setCategory(ModCategory.PLAYER);
		state = State.WALK;
	}

	@Override
	public void onRenderSkia(float partialTicks) {

		this.setMovable(hudSetting.isEnabled());

		if (hudSetting.isEnabled()) {
			this.draw();
		}
	}

	@Override
	public void onClientTick() {

		boolean down = Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode());

		if (alwaysSetting.isEnabled() || mc.currentScreen != null) {
			return;
		}

		if (down) {
			if (!wasDown) {

				startTime = System.currentTimeMillis();

				if (state.equals(State.TOGGLED)) {
					state = State.HELD;
				} else {
					state = State.TOGGLED;
				}
			} else if ((System.currentTimeMillis() - startTime) > 250) {
				state = State.HELD;
			}
		} else if (state.equals(State.HELD) && mc.thePlayer.isSprinting()) {
			state = State.VANILLA;
		} else if ((state.equals(State.VANILLA) || state.equals(State.HELD)) && !mc.thePlayer.isSprinting()) {
			state = State.WALK;
		}

		wasDown = down;
	}

	@Override
	public void onUpdate() {
		KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(),
				state.equals(State.HELD) || state.equals(State.TOGGLED) || alwaysSetting.isEnabled());
	}

	@Override
	public void onEnable() {
		EventBus.getInstance().registers(this, ClientTickEvent.ID, RenderSkiaEvent.ID, PlayerUpdateEvent.ID);
	}

	@Override
	public void onDisable() {
		EventBus.getInstance().unregisters(this, ClientTickEvent.ID, RenderSkiaEvent.ID, PlayerUpdateEvent.ID);
	}

	@Override
	public String getText() {

		String prefix = "Sprinting";

		if (alwaysSetting.isEnabled()) {
			return prefix + " (Always)";
		}

		if (state.equals(State.WALK)) {
			return "Walking";
		}

		return prefix + " (" + state.name + ")";
	}

	@Override
	public String getIcon() {

		if (alwaysSetting.isEnabled()) {
			return Icon.SPRINT;
		}

		return state.icon;
	}

	private enum State {
		WALK("Walking", Icon.DIRECTIONS_WALK), VANILLA("Vanilla", Icon.DIRECTIONS_WALK), HELD("Key Held", Icon.SPRINT),
		TOGGLED("Toggled", Icon.SPRINT);

		private String name;
		private String icon;

		private State(String name, String icon) {
			this.name = name;
			this.icon = icon;
		}
	}
}
