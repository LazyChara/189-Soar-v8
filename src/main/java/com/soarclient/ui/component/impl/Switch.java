package com.soarclient.ui.component.impl;

import java.awt.Color;

import com.soarclient.Soar;
import com.soarclient.animation.SimpleAnimation;
import com.soarclient.management.color.api.ColorPalette;
import com.soarclient.skia.Skia;
import com.soarclient.ui.component.Component;
import com.soarclient.ui.component.handler.impl.SwitchHandler;
import com.soarclient.utils.ColorUtils;
import com.soarclient.utils.mouse.MouseUtils;

public class Switch extends Component {

	private SimpleAnimation enableAnimation = new SimpleAnimation();
	private SimpleAnimation pressAnimation = new SimpleAnimation();
	private SimpleAnimation focusAnimation = new SimpleAnimation();
	private boolean pressed;
	private boolean enabled;

	public Switch(float x, float y, boolean enabled) {
		super(x, y);
		this.width = 52;
		this.height = 32;
		this.enabled = enabled;
		this.pressed = false;
	}

	@Override
	public void draw(int mouseX, int mouseY) {

		ColorPalette palette = Soar.getInstance().getColorManager().getPalette();
		boolean focus = MouseUtils.isInside(mouseX, mouseY, x, y, width, height);

		Skia.drawRoundedRect(x, y, width, height, 16, palette.getSurfaceContainerHighest());
		Skia.drawOutline(x, y, width, height, 16, 2, palette.getOutline());

		enableAnimation.onTick(enabled ? 1 : 0, 12);
		pressAnimation.onTick(pressed ? 1 : 0, 12);
		focusAnimation.onTick(focus ? 1 : 0, 10);

		Skia.drawRoundedRect(x, y, width, height, 16,
				ColorUtils.applyAlpha(palette.getPrimary(), enableAnimation.getValue()));

		Color fc = enabled ? palette.getPrimaryContainer() : palette.getOnSurfaceVariant();
		Color ec = enabled ? palette.getOnPrimary() : palette.getOutline();

		Color pc = enabled ? palette.getPrimary() : palette.getOnSurface();

		Skia.drawCircle(x + 16 + (20 * enableAnimation.getValue()), y + 16,
				8 + (enableAnimation.getValue() * 4) + (pressAnimation.getValue() * 1), ec);
		Skia.drawCircle(x + 16 + (20 * enableAnimation.getValue()), y + 16,
				8 + (enableAnimation.getValue() * 4) + (pressAnimation.getValue() * 1),
				ColorUtils.applyAlpha(fc, focusAnimation.getValue()));
		Skia.drawCircle(x + 16 + (20 * enableAnimation.getValue()), y + 16,
				8 + (enableAnimation.getValue() * 4) + (pressAnimation.getValue() * 10),
				ColorUtils.applyAlpha(pc, pressAnimation.getValue() * 0.12F));
	}

	@Override
	public void mousePressed(int mouseX, int mouseY, int mouseButton) {
		if (MouseUtils.isInside(mouseX, mouseY, x, y, width, height) && mouseButton == 0) {
			pressed = true;
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

		if (MouseUtils.isInside(mouseX, mouseY, x, y, width, height) && mouseButton == 0) {
			enabled = !enabled;

			if (handler instanceof SwitchHandler) {

				SwitchHandler sHandler = (SwitchHandler) handler;

				if (enabled) {
					sHandler.onEnabled();
				} else {
					sHandler.onDisabled();
				}
			}
		}

		pressed = false;
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) {
	}
}
