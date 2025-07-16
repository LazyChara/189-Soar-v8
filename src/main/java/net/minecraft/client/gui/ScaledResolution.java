package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

public class ScaledResolution {

	private static ScaledResolution instance;
	private static int lastDisplayWidth;
	private static int lastDisplayHeight;

	private final double scaledWidthD;
	private final double scaledHeightD;
	private int scaledWidth;
	private int scaledHeight;
	private int scaleFactor;

	private ScaledResolution(Minecraft minecraft) {
		this.scaledWidth = minecraft.displayWidth;
		this.scaledHeight = minecraft.displayHeight;
		this.scaleFactor = 1;
		boolean flag = minecraft.isUnicode();
		int i = minecraft.gameSettings.guiScale;

		if (i == 0) {
			i = 1000;
		}

		while (this.scaleFactor < i && this.scaledWidth / (this.scaleFactor + 1) >= 320
				&& this.scaledHeight / (this.scaleFactor + 1) >= 240) {
			++this.scaleFactor;
		}

		if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
			--this.scaleFactor;
		}

		this.scaledWidthD = (double) this.scaledWidth / (double) this.scaleFactor;
		this.scaledHeightD = (double) this.scaledHeight / (double) this.scaleFactor;
		this.scaledWidth = MathHelper.ceiling_double_int(this.scaledWidthD);
		this.scaledHeight = MathHelper.ceiling_double_int(this.scaledHeightD);
	}

	public static ScaledResolution get(Minecraft minecraft) {
		if (instance == null || minecraft.displayWidth != lastDisplayWidth
				|| minecraft.displayHeight != lastDisplayHeight) {
			instance = new ScaledResolution(minecraft);
			lastDisplayWidth = minecraft.displayWidth;
			lastDisplayHeight = minecraft.displayHeight;
		}
		return instance;
	}
	
	public static ScaledResolution create(Minecraft minecraft) {
		return instance = new ScaledResolution(minecraft);
	}

	public int getScaledWidth() {
		return this.scaledWidth;
	}

	public int getScaledHeight() {
		return this.scaledHeight;
	}

	public double getScaledWidth_double() {
		return this.scaledWidthD;
	}

	public double getScaledHeight_double() {
		return this.scaledHeightD;
	}

	public int getScaleFactor() {
		return this.scaleFactor;
	}
}