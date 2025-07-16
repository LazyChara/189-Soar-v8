package net.minecraft.client.settings;

import java.util.List;
import java.util.Set;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.soarclient.management.mod.impl.player.SnapTapMod;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class KeyBinding implements Comparable<KeyBinding> {
	private static final List<KeyBinding> keybindArray = Lists.newArrayList();
	private static final Int2ObjectOpenHashMap<KeyBinding> hash = new Int2ObjectOpenHashMap<>();
	private static final Set<String> keybindSet = Sets.newHashSet();
	private final String keyDescription;
	private final int keyCodeDefault;
	private final String keyCategory;
	private int keyCode;

	/** Is the key held down? */
	private boolean pressed;
	private int pressTime;

	public static void onTick(int keyCode) {
		if (keyCode != 0) {
			KeyBinding keybinding = hash.get(keyCode);

			if (keybinding != null) {
				++keybinding.pressTime;
			}
		}
	}

	public static void setKeyBindState(int keyCode, boolean pressed) {

		Minecraft mc = Minecraft.getMinecraft();
		SnapTapMod mod = SnapTapMod.getInstance();

		if (mod.isEnabled()) {
			if (keyCode == mc.gameSettings.keyBindLeft.getKeyCode()) {
				if (pressed) {
					mod.setLeftPressTime(System.currentTimeMillis());
				} else {
					mod.setLeftPressTime(0);
				}
			} else if (keyCode == mc.gameSettings.keyBindRight.getKeyCode()) {
				if (pressed) {
					mod.setRightPressTime(System.currentTimeMillis());
				} else {
					mod.setRightPressTime(0);
				}
			} else if (keyCode == mc.gameSettings.keyBindForward.getKeyCode()) {
				if (pressed) {
					mod.setForwardPressTime(System.currentTimeMillis());
				} else {
					mod.setForwardPressTime(0);
				}
			} else if (keyCode == mc.gameSettings.keyBindBack.getKeyCode()) {
				if (pressed) {
					mod.setBackPressTime(System.currentTimeMillis());
				} else {
					mod.setBackPressTime(0);
				}
			}
		}

		if (keyCode != 0) {
			KeyBinding keybinding = hash.get(keyCode);

			if (keybinding != null) {
				keybinding.pressed = pressed;
			}
		}
	}

	public static void unPressAllKeys() {
		for (KeyBinding keybinding : keybindArray) {
			keybinding.unpressKey();
		}
	}

	public static void resetKeyBindingArrayAndHash() {
		hash.clear();

		for (KeyBinding keybinding : keybindArray) {
			hash.put(keybinding.keyCode, keybinding);
		}
	}

	public static Set<String> getKeybinds() {
		return keybindSet;
	}

	public KeyBinding(String description, int keyCode, String category) {
		this.keyDescription = description;
		this.keyCode = keyCode;
		this.keyCodeDefault = keyCode;
		this.keyCategory = category;
		keybindArray.add(this);
		hash.put(keyCode, this);
		keybindSet.add(category);
	}

	/**
	 * Returns true if the key is pressed (used for continuous querying). Should be
	 * used in tickers.
	 */
	public boolean isKeyDown() {

		SnapTapMod mod = SnapTapMod.getInstance();

		if (mod.isEnabled()) {

			if (this.keyCodeDefault == Keyboard.KEY_A) {

				if (this.pressed) {
					if (mod.getRightPressTime() == 0) {
						return true;
					}

					return mod.getRightPressTime() <= mod.getLeftPressTime();
				}
			} else if (this.keyCodeDefault == Keyboard.KEY_D) {

				if (this.pressed) {
					if (mod.getLeftPressTime() == 0) {
						return true;
					}

					return mod.getLeftPressTime() <= mod.getRightPressTime();
				}
			} else if (this.keyCodeDefault == Keyboard.KEY_W) {

				if (this.pressed) {
					if (mod.getBackPressTime() == 0) {
						return true;
					}

					return mod.getBackPressTime() <= mod.getForwardPressTime();
				}
			} else if (this.keyCodeDefault == Keyboard.KEY_S) {

				if (this.pressed) {
					if (mod.getForwardPressTime() == 0) {
						return true;
					}

					return mod.getForwardPressTime() <= mod.getBackPressTime();
				}
			}
		}

		return this.pressed;
	}

	public boolean getRealIsKeyDown() {
		return this.pressed;
	}

	public String getKeyCategory() {
		return this.keyCategory;
	}

	/**
	 * Returns true on the initial key press. For continuous querying use
	 * {@link isKeyDown()}. Should be used in key events.
	 */
	public boolean isPressed() {
		if (this.pressTime == 0) {
			return false;
		} else {
			--this.pressTime;
			return true;
		}
	}

	private void unpressKey() {
		this.pressTime = 0;
		this.pressed = false;
	}

	public String getKeyDescription() {
		return this.keyDescription;
	}

	public int getKeyCodeDefault() {
		return this.keyCodeDefault;
	}

	public int getKeyCode() {
		return this.keyCode;
	}

	public void setKeyCode(int keyCode) {
		this.keyCode = keyCode;
	}

	public int compareTo(KeyBinding p_compareTo_1_) {
		int i = I18n.format(this.keyCategory).compareTo(I18n.format(p_compareTo_1_.keyCategory));

		if (i == 0) {
			i = I18n.format(this.keyDescription).compareTo(I18n.format(p_compareTo_1_.keyDescription));
		}

		return i;
	}
}
