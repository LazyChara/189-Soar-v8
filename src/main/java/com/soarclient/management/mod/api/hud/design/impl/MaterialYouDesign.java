package com.soarclient.management.mod.api.hud.design.impl;

import java.awt.Color;

import com.soarclient.Soar;
import com.soarclient.management.color.api.ColorPalette;
import com.soarclient.management.mod.api.hud.design.HUDDesign;
import com.soarclient.skia.Skia;
import com.soarclient.utils.ColorUtils;

import io.github.humbleui.skija.Font;

public class MaterialYouDesign extends HUDDesign {

	public MaterialYouDesign() {
		super("design.materialyou");
	}

	@Override
	public void drawBackground(float x, float y, float width, float height, float radius) {

		ColorPalette palette = Soar.getInstance().getColorManager().getPalette();

		Skia.drawShadow(x, y, width, height, radius);
		Skia.drawGradientRoundedRect(x, y, width, height, radius,
				ColorUtils.applyAlpha(palette.getPrimaryContainer(), 180),
				ColorUtils.applyAlpha(palette.getTertiaryContainer(), 180));
	}

	@Override
	public void drawText(String text, float x, float y, Font font) {
		Skia.drawText(text, x, y, getTextColor(), font);
	}

	@Override
	public Color getTextColor() {
		return Color.WHITE;
	}

	@Override
	public Color getOnTextColor() {
		return Color.BLACK;
	}
}
