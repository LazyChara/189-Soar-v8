package com.soarclient.ui.component.impl;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.soarclient.Soar;
import com.soarclient.management.color.api.ColorPalette;
import com.soarclient.skia.Skia;
import com.soarclient.skia.font.Fonts;
import com.soarclient.ui.component.Component;
import com.soarclient.ui.component.api.PressAnimation;
import com.soarclient.ui.component.handler.impl.KeybindHandler;
import com.soarclient.utils.mouse.MouseUtils;

public class Keybind extends Component {

	private PressAnimation pressAnimation = new PressAnimation();
	private int[] pressedPos;

	private boolean binding;
	private int keyCode;

	public Keybind(float x, float y, int keyCode) {
		super(x, y);
		this.keyCode = keyCode;
		width = 126;
		height = 32;
		pressedPos = new int[] { 0, 0 };
	}

	@Override
	public void draw(int mouseX, int mouseY) {

		ColorPalette palette = Soar.getInstance().getColorManager().getPalette();

		Skia.drawRoundedRect(x, y, width, height, 12, palette.getPrimary());
		Skia.save();
		Skia.clip(x, y, width, height, 12);
		pressAnimation.draw(x + pressedPos[0], y + pressedPos[1], width, height, palette.getPrimaryContainer(), 0.12F);
		Skia.restore();
		Skia.drawFullCenteredText(
				binding ? "..." : keyCode < 0 ? Mouse.getButtonName(keyCode + 100) : Keyboard.getKeyName(keyCode),
				x + (width / 2), y + (height / 2), palette.getSurface(), Fonts.getMedium(14));
	}

	@Override
	public void mousePressed(int mouseX, int mouseY, int mouseButton) {
		if (MouseUtils.isInside(mouseX, mouseY, x, y, width, height) && mouseButton == 0) {
			pressedPos = new int[] { mouseX - (int) x, mouseY - (int) y };
			pressAnimation.mousePressed();
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

		if (MouseUtils.isInside(mouseX, mouseY, x, y, width, height)) {

			if (mouseButton == 0 && !binding) {
				binding = true;
			} else if (mouseButton == 2 && binding) {
				setKeyCode(Keyboard.KEY_NONE);
				binding = false;
			} else if (binding && mouseButton != 0 && mouseButton != 1 && mouseButton != 2) {
				setKeyCode(-100 + mouseButton);
				binding = false;
			} else {
				binding = false;
			}

		} else if (binding) {

			if (mouseButton != 0 && mouseButton != 1 && mouseButton != 2) {
				setKeyCode(-100 + mouseButton);
			}

			binding = false;
		}

		pressAnimation.mouseReleased();
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) {
		if (binding) {
			setKeyCode(keyCode);
			this.binding = false;
		}
	}

	public int getKeyCode() {
		return keyCode;
	}

	public void setKeyCode(int keyCode) {

		this.keyCode = keyCode;

		if (handler instanceof KeybindHandler) {
			((KeybindHandler) handler).onBinded(keyCode);
		}
	}
}
