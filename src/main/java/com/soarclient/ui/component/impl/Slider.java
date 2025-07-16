package com.soarclient.ui.component.impl;

import com.soarclient.Soar;
import com.soarclient.animation.SimpleAnimation;
import com.soarclient.management.color.api.ColorPalette;
import com.soarclient.skia.Skia;
import com.soarclient.skia.font.Fonts;
import com.soarclient.ui.component.Component;
import com.soarclient.ui.component.handler.impl.SliderHandler;
import com.soarclient.utils.ColorUtils;
import com.soarclient.utils.MathUtils;
import com.soarclient.utils.mouse.MouseUtils;

public class Slider extends Component {

	private SimpleAnimation slideAnimation = new SimpleAnimation();
	private SimpleAnimation valueAnimation = new SimpleAnimation();

	private boolean dragging;
	private float value, minValue, maxValue;
	private float step;

	public Slider(float x, float y, float width, float value, float minValue, float maxValue, float step) {
		super(x, y);
		this.width = width;
		this.height = 38;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.step = step;
		this.setValue(value);
	}

	@Override
	public void draw(int mouseX, int mouseY) {

		ColorPalette palette = Soar.getInstance().getColorManager().getPalette();

		slideAnimation.onTick(((getValue() - minValue) / (maxValue - minValue)) * width, 20);

		float padding = 6;
		float selWidth = 4;
		float barHeight = 16;
		float offsetY = (height / 2) - (barHeight / 2);

		float slideValue = Math.abs(slideAnimation.getValue());
		boolean focus = MouseUtils.isInside(mouseX, mouseY, x, y, width, height);

		Skia.drawRoundedRect(x + slideValue - (selWidth / 2), y, selWidth, height, 3, palette.getPrimary());

		Skia.save();
		Skia.clip(x, y, width, height, 0);
		Skia.drawRoundedRectVarying(x, y + offsetY, slideValue - (selWidth / 2) - padding, barHeight, 8, 4, 4, 8,
				palette.getPrimary());
		Skia.drawRoundedRectVarying(x + padding + (selWidth / 2) + slideValue, y + offsetY,
				width - slideValue - padding, barHeight, 4, 8, 8, 4, palette.getPrimaryContainer());
		Skia.restore();

		valueAnimation.onTick(focus || dragging ? 1 : 0, 16);

		float centerX = (x + slideValue - (selWidth / 2));
		float pWidth = 38;
		float pHeight = 28;

		Skia.save();
		Skia.translate(0, 10 - (valueAnimation.getValue() * 10));
		Skia.drawRoundedRect(centerX - (pWidth / 2) + (selWidth / 2), y - pHeight - 6, pWidth, pHeight, 18,
				ColorUtils.applyAlpha(palette.getOnSurface(), valueAnimation.getValue()));
		Skia.drawFullCenteredText(String.valueOf(getValue()), centerX - (pWidth / 2) + (selWidth / 2) + (pWidth / 2),
				y - (pHeight / 2) - 6, ColorUtils.applyAlpha(palette.getSurface(), valueAnimation.getValue()),
				Fonts.getRegular(10));
		Skia.restore();

		if (dragging) {

			float rawValue = Math.min(1, Math.max(0, (mouseX - x) / width));
			float actualValue = rawValue * (maxValue - minValue) + minValue;
			float steppedValue = Math.round(actualValue / step) * step;

			value = (steppedValue - minValue) / (maxValue - minValue);

			if (handler instanceof SliderHandler) {
				((SliderHandler) handler).onValueChanged(getValue());
			}
		}
	}

	@Override
	public void mousePressed(int mouseX, int mouseY, int mouseButton) {

		if (MouseUtils.isInside(mouseX, mouseY, x, y, width, height) && mouseButton == 0) {

			dragging = true;

			if (handler instanceof SliderHandler) {
				((SliderHandler) handler).onPressed(getValue());
			}
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		if (dragging) {

			dragging = false;

			if (handler instanceof SliderHandler) {
				((SliderHandler) handler).onReleased(getValue());
			}
		}
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) {
	}

	public float getValue() {
		return MathUtils.roundToPlace(value * (maxValue - minValue) + minValue, 2);
	}

	public void setValue(float value) {
		float steppedValue = Math.round(value / step) * step;
		this.value = (steppedValue - minValue) / (maxValue - minValue);
	}
}
