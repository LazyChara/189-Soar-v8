package com.soarclient.ui.component.impl;

import java.awt.Color;

import com.soarclient.Soar;
import com.soarclient.animation.SimpleAnimation;
import com.soarclient.libraries.material3.hct.Hct;
import com.soarclient.management.color.api.ColorPalette;
import com.soarclient.skia.Skia;
import com.soarclient.ui.component.Component;
import com.soarclient.ui.component.handler.impl.HctColorPickerHandler;
import com.soarclient.utils.mouse.MouseUtils;

public class HctColorPicker extends Component {

	private SimpleAnimation slideAnimation = new SimpleAnimation();

	private Hct hct;
	private float minValue, maxValue, value;
	private boolean dragging;

	public HctColorPicker(float x, float y, Hct hct) {
		super(x, y);
		this.hct = hct;
		minValue = 0;
		maxValue = 360;
		value = (float) (hct.getHue() - minValue) / (maxValue - minValue);
		width = 126;
		height = 32;
	}

	@Override
	public void draw(int mouseX, int mouseY) {

		ColorPalette palette = Soar.getInstance().getColorManager().getPalette();

		slideAnimation.onTick(width * value, 20);

		Skia.drawRoundedImage("hue-h.png", x, y, width, height, 12);
		Skia.drawCircle(x + slideAnimation.getValue(), y + (height / 2), 9.8F,
				Color.getHSBColor((float) (hct.getHue() / 360), 1, 1));
		Skia.drawCircle(x + slideAnimation.getValue(), y + (height / 2), 10, 2F, palette.getSurface());

		if (dragging) {

			value = Math.min(1, Math.max(0, (mouseX - x) / width));
			hct = Hct.from((value * (maxValue - minValue) + minValue), hct.getChroma(), hct.getTone());

			onPicking(hct);
		}
	}

	@Override
	public void mousePressed(int mouseX, int mouseY, int mouseButton) {

		if (MouseUtils.isInside(mouseX, mouseY, x, y, width, height) && mouseButton == 0) {
			dragging = true;
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		dragging = false;
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) {
	}

	private void onPicking(Hct hct) {
		if (handler instanceof HctColorPickerHandler) {
			((HctColorPickerHandler) handler).onPicking(hct);
		}
	}
}
