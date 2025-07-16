package com.soarclient.gui.api;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.soarclient.skia.Skia;
import com.soarclient.skia.context.SkiaContext;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class SimpleSoarGui {

	protected Minecraft mc = Minecraft.getMinecraft();

	private boolean mcScale;

	public SimpleSoarGui(boolean mcScale) {
		this.mcScale = mcScale;
	}

	public void init() {
	}

	public void drawOpenGL(int mouseX, int mouseY) {
	}

	public void draw(int mouseX, int mouseY) {
	}

	public void mousePressed(int mouseX, int mouseY, int mouseButton) {
	}

	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
	}

	public void keyTyped(char typedChar, int keyCode) {
	}

	public void onClosed() {
	}

	public GuiScreen build() {
		return new GuiScreen() {

			@Override
			public void initGui() {
				SimpleSoarGui.this.init();
			}

			@Override
			public void drawScreen(int mouseX, int mouseY, float partialTicks) {

				ScaledResolution sr = ScaledResolution.get(mc);

				SimpleSoarGui.this.drawOpenGL(mcScale ? mouseX : (int) Mouse.getX(),
						mcScale ? mouseY : (int) Display.getHeight() - Mouse.getY());

				SkiaContext.draw((context) -> {

					Skia.save();

					if (mcScale) {
						Skia.scale(sr.getScaleFactor());
					}

					SimpleSoarGui.this.draw(mcScale ? mouseX : (int) Mouse.getX(),
							mcScale ? mouseY : (int) Display.getHeight() - Mouse.getY());
					Skia.restore();
				});
			}

			@Override
			public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
				SimpleSoarGui.this.mousePressed(mcScale ? mouseX : (int) Mouse.getX(),
						mcScale ? mouseY : (int) Display.getHeight() - Mouse.getY(), mouseButton);
			}

			@Override
			public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
				SimpleSoarGui.this.mouseReleased(mcScale ? mouseX : (int) Mouse.getX(),
						mcScale ? mouseY : (int) Display.getHeight() - Mouse.getY(), mouseButton);
			}

			@Override
			public void keyTyped(char typedChar, int keyCode) {
				SimpleSoarGui.this.keyTyped(typedChar, keyCode);
			}

			@Override
			public void onGuiClosed() {
				SimpleSoarGui.this.onClosed();
			}

			@Override
			public boolean doesGuiPauseGame() {
				return false;
			}
		};
	}
}
