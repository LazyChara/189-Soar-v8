package com.soarclient.viasoar.gui;

import java.util.List;

import com.soarclient.viasoar.ViaSoar;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

public class AsyncVersionSlider extends GuiButton {

	private float dragValue = (float) getProtocols().indexOf(ViaSoar.getManager().getTargetVersion())
			/ (getProtocols().size() - 1);

	private final List<ProtocolVersion> values;
	private float sliderValue;
	public boolean dragging;

	public AsyncVersionSlider(int buttonId, int x, int y, int widthIn, int heightIn) {
		super(buttonId, x, y, Math.max(widthIn, 110), heightIn, "");
		this.values = getProtocols();
		this.sliderValue = dragValue;
		this.displayString = values.get((int) Math.ceil(this.sliderValue * (values.size() - 1))).getName();
	}

	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		super.drawButton(mc, mouseX, mouseY);
	}

	protected int getHoverState(boolean mouseOver) {
		return 0;
	}

	protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {

		if (this.visible) {
			if (this.dragging) {
				this.sliderValue = (float) (mouseX - (this.xPosition + 4)) / (float) (this.width - 8);
				this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
				this.dragValue = sliderValue;

				int selectedProtocolIndex = (int) Math.ceil(this.sliderValue * (values.size() - 1));
				this.displayString = values.get(selectedProtocolIndex).getName();
				ViaSoar.getManager().setTargetVersion(values.get(selectedProtocolIndex));
			}

			mc.getTextureManager().bindTexture(buttonTextures);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawTexturedModalRect(this.xPosition + (int) (this.sliderValue * (float) (this.width - 8)),
					this.yPosition, 0, 66, 4, 20);
			this.drawTexturedModalRect(this.xPosition + (int) (this.sliderValue * (float) (this.width - 8)) + 4,
					this.yPosition, 196, 66, 4, 20);
		}
	}

	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if (super.mousePressed(mc, mouseX, mouseY)) {
			this.sliderValue = (float) (mouseX - (this.xPosition + 4)) / (float) (this.width - 8);
			this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
			this.dragValue = sliderValue;

			int selectedProtocolIndex = (int) Math.ceil(this.sliderValue * (values.size() - 1));
			this.displayString = values.get(selectedProtocolIndex).getName();
			ViaSoar.getManager().setTargetVersion(values.get(selectedProtocolIndex));
			this.dragging = true;
			return true;
		} else {
			return false;
		}
	}

	public void mouseReleased(int mouseX, int mouseY) {
		this.dragging = false;
	}

	public void setVersion(ProtocolVersion version) {
		this.dragValue = (float) getProtocols().indexOf(version) / (getProtocols().size() - 1);
		this.sliderValue = this.dragValue;

		int selectedProtocolIndex = (int) Math.ceil(this.sliderValue * (values.size() - 1));
		this.displayString = values.get(selectedProtocolIndex).getName();
	}

	private static List<ProtocolVersion> getProtocols() {
		return ViaSoar.getVersions();
	}
}