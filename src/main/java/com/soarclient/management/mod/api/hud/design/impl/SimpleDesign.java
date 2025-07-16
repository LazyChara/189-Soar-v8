package com.soarclient.management.mod.api.hud.design.impl;

import java.awt.Color;

import com.soarclient.management.mod.api.hud.design.HUDDesign;
import com.soarclient.skia.Skia;

import io.github.humbleui.skija.Font;

public class SimpleDesign extends HUDDesign {

	public SimpleDesign() {
		super("design.simple");
	}

	@Override
	public void drawBackground(float x, float y, float width, float height, float radius) {
		Skia.drawShadow(x, y, width, height, radius);
		Skia.drawRoundedRect(x, y, width, height, radius, new Color(0, 0, 0, 100));
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
