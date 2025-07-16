package com.soarclient.management.mod.impl.player;

import org.lwjgl.input.Keyboard;

import com.soarclient.event.EventBus;
import com.soarclient.event.impl.ClientTickEventListener;
import com.soarclient.management.mod.Mod;
import com.soarclient.management.mod.ModCategory;
import com.soarclient.management.mod.settings.impl.KeybindSetting;
import com.soarclient.skia.font.Icon;
import com.soarclient.utils.PlayerUtils;

import net.minecraft.init.Items;

public class QuickSwitchMod extends Mod implements ClientTickEventListener {

	private KeybindSetting swordSetting = new KeybindSetting("setting.sowrd", "setting.quickswitch.sword.description",
			Icon.KEYBOARD, this, Keyboard.KEY_NONE);
	private KeybindSetting blockSetting = new KeybindSetting("setting.block", "setting.quickswitch.block.description",
			Icon.KEYBOARD, this, Keyboard.KEY_NONE);
	private KeybindSetting rodSetting = new KeybindSetting("setting.rod", "setting.quickswitch.rod.description",
			Icon.KEYBOARD, this, Keyboard.KEY_NONE);
	private KeybindSetting axeSetting = new KeybindSetting("setting.axe", "setting.quickswitch.axe.description",
			Icon.KEYBOARD, this, Keyboard.KEY_NONE);
	private KeybindSetting pickaxeSetting = new KeybindSetting("setting.pickaxe",
			"setting.quickswitch.pickaxe.description", Icon.KEYBOARD, this, Keyboard.KEY_NONE);
	private KeybindSetting bowSetting = new KeybindSetting("setting.bow", "setting.quickswitch.bow.description",
			Icon.KEYBOARD, this, Keyboard.KEY_NONE);
	private KeybindSetting lavaSetting = new KeybindSetting("setting.lava", "setting.quickswitch.lava.descriptio",
			Icon.KEYBOARD, this, Keyboard.KEY_NONE);
	private KeybindSetting waterSetting = new KeybindSetting("setting.water", "setting.quickswitch.water.description",
			Icon.KEYBOARD, this, Keyboard.KEY_NONE);

	public QuickSwitchMod() {
		super("mod.quickswitch.name", "mod.quickswitch.description", Icon.SWORDS, ModCategory.PLAYER);
	}

	@Override
	public void onClientTick() {

		if (swordSetting.isKeyDown()) {
			setCurrentItem(PlayerUtils.getBestSword(mc.thePlayer));
		}

		if (blockSetting.isKeyDown()) {
			setCurrentItem(PlayerUtils.getBestBlock(mc.thePlayer));
		}

		if (rodSetting.isKeyDown()) {
			setCurrentItem(PlayerUtils.getItemSlot(Items.fishing_rod));
		}

		if (lavaSetting.isKeyDown()) {
			setCurrentItem(PlayerUtils.getItemSlot(Items.lava_bucket));
		}

		if (waterSetting.isKeyDown()) {
			setCurrentItem(PlayerUtils.getItemSlot(Items.water_bucket));
		}

		if (axeSetting.isKeyDown()) {
			setCurrentItem(PlayerUtils.getBestAxe(mc.thePlayer));
		}

		if (pickaxeSetting.isKeyDown()) {
			setCurrentItem(PlayerUtils.getBestPickaxe(mc.thePlayer));
		}

		if (bowSetting.isKeyDown()) {
			setCurrentItem(PlayerUtils.getBestBow(mc.thePlayer));
		}
	}

	private void setCurrentItem(int slot) {
		mc.thePlayer.inventory.currentItem = slot;
	}

	@Override
	public void onEnable() {
		EventBus.getInstance().register(this, ClientTickEvent.ID);
	}

	@Override
	public void onDisable() {
		EventBus.getInstance().unregister(this, ClientTickEvent.ID);
	}
}
