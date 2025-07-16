package com.soarclient.ui.component.impl;

import java.awt.Color;

import com.soarclient.Soar;
import com.soarclient.management.color.api.ColorPalette;
import com.soarclient.skia.Skia;
import com.soarclient.skia.font.Fonts;
import com.soarclient.ui.component.Component;
import com.soarclient.ui.component.api.PressAnimation;
import com.soarclient.ui.component.handler.impl.ButtonHandler;
import com.soarclient.utils.language.I18n;
import com.soarclient.utils.mouse.MouseUtils;

import io.github.humbleui.types.Rect;

public class Button extends Component {

	private PressAnimation pressAnimation = new PressAnimation();

	private int[] pressedPos;
	private String text;
	private Style style;

	public Button(String text, float x, float y, Style style) {
		super(x, y);
		this.text = text;
		this.height = 40;
		this.style = style;

		Rect bounds = Skia.getTextBounds(I18n.get(text), Fonts.getRegular(16));

		this.width = bounds.getWidth() + (24 * 2);
		pressedPos = new int[] { 0, 0 };
	}

	@Override
	public void draw(int mouseX, int mouseY) {

		Color[] colors = getColor();

		Skia.drawRoundedRect(x, y, width, height, 25, colors[0]);
		Skia.save();
		Skia.clip(x, y, width, height, 25);
		pressAnimation.draw(x + pressedPos[0], y + pressedPos[1], width, height, colors[1], 0.12F);
		Skia.restore();
		Skia.drawFullCenteredText(I18n.get(text), x + (width / 2), y + (height / 2), colors[1], Fonts.getRegular(16));
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
		if (MouseUtils.isInside(mouseX, mouseY, x, y, width, height) && mouseButton == 0) {
			if (handler instanceof ButtonHandler) {
				((ButtonHandler) handler).onAction();
			}
		}
		pressAnimation.mouseReleased();
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) {
	}

	private Color[] getColor() {

		ColorPalette palette = Soar.getInstance().getColorManager().getPalette();

		switch (style) {
		case ELEVATED:
			return new Color[] { palette.getSurfaceContainerLow(), palette.getPrimary() };
		case FILLED:
			return new Color[] { palette.getPrimary(), palette.getOnPrimary() };
		case TONAL:
			return new Color[] { palette.getSecondaryContainer(), palette.getOnSecondaryContainer() };
		default:
			return new Color[] { Color.RED, Color.RED };
		}
	}

	public enum Style {
		FILLED, ELEVATED, TONAL
	}
}
