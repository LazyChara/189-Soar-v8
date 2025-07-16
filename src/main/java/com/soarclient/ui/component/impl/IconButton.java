package com.soarclient.ui.component.impl;

import java.awt.Color;

import com.soarclient.Soar;
import com.soarclient.animation.SimpleAnimation;
import com.soarclient.management.color.api.ColorPalette;
import com.soarclient.skia.Skia;
import com.soarclient.skia.font.Fonts;
import com.soarclient.ui.component.Component;
import com.soarclient.ui.component.api.PressAnimation;
import com.soarclient.ui.component.handler.impl.ButtonHandler;
import com.soarclient.utils.ColorUtils;
import com.soarclient.utils.mouse.MouseUtils;

public class IconButton extends Component {

	private SimpleAnimation focusAnimation = new SimpleAnimation();
	private PressAnimation pressAnimation;

	private String icon;
	private Size size;
	private Style style;
	private int[] pressedPos;

	public IconButton(String icon, float x, float y, Size size, Style style) {
		super(x, y);

		this.icon = icon;
		this.size = size;
		this.style = style;
		float[] s = getPanelSize();

		width = s[0];
		height = s[1];
		pressedPos = new int[] { 0, 0 };
		pressAnimation = new PressAnimation();
	}

	@Override
	public void draw(int mouseX, int mouseY) {

		boolean focus = MouseUtils.isInside(mouseX, mouseY, x, y, width, height);

		Color[] c = getColor();

		focusAnimation.onTick(focus ? 1F : 0, 10);

		Skia.save();
		Skia.clip(x, y, width, height, getRadius());
		Skia.drawRoundedRect(x, y, width, height, getRadius(), c[0]);
		Skia.drawRoundedRect(x, y, width, height, getRadius(),
				ColorUtils.applyAlpha(c[1], focusAnimation.getValue() * 0.08F));
		pressAnimation.draw(x + pressedPos[0], y + pressedPos[1], width, height, c[1], 0.12F);

		Skia.drawFullCenteredText(icon, x + (width / 2), y + (height / 2), c[1], Fonts.getIconFill(getFontSize()));

		Skia.restore();
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

	private float[] getPanelSize() {
		switch (size) {
		case LARGE:
			return new float[] { 64, 64 };
		case NORMAL:
			return new float[] { 56, 56 };
		case SMALL:
			return new float[] { 40, 40 };
		default:
			return new float[] { 0, 0 };
		}
	}

	private float getFontSize() {
		switch (size) {
		case LARGE:
			return 30;
		case NORMAL:
			return 24;
		case SMALL:
			return 24;
		default:
			return 0F;
		}
	}

	private float getRadius() {
		switch (size) {
		case LARGE:
			return 18;
		case NORMAL:
			return 16;
		case SMALL:
			return 12;
		default:
			return 0F;
		}
	}

	private Color[] getColor() {

		ColorPalette palette = Soar.getInstance().getColorManager().getPalette();

		switch (style) {
		case PRIMARY:
			return new Color[] { palette.getPrimaryContainer(), palette.getOnPrimaryContainer() };
		case SECONDARY:
			return new Color[] { palette.getSecondaryContainer(), palette.getOnSecondaryContainer() };
		case SURFACE:
			return new Color[] { palette.getSurfaceContainer(), palette.getPrimary() };
		case TERTIARY:
			return new Color[] { palette.getTertiaryContainer(), palette.getOnTertiaryContainer() };
		default:
			return new Color[] { Color.RED, Color.RED };
		}
	}

	public enum Size {
		SMALL, NORMAL, LARGE;
	}

	public enum Style {
		SURFACE, PRIMARY, SECONDARY, TERTIARY;
	}
}
