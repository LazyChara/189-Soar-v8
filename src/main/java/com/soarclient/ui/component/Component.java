package com.soarclient.ui.component;

import com.soarclient.ui.component.handler.ComponentHandler;

import net.minecraft.client.Minecraft;

public abstract class Component {

	protected Minecraft mc = Minecraft.getMinecraft();
	protected ComponentHandler handler;
	protected float x, y, width, height;

	public Component(float x, float y) {
		this.x = x;
		this.y = y;
		this.handler = new ComponentHandler() {
		};
	}

	public abstract void draw(int mouseX, int mouseY);

	public abstract void mousePressed(int mouseX, int mouseY, int mouseButton);

	public abstract void mouseReleased(int mouseX, int mouseY, int mouseButton);

	public abstract void keyTyped(char typedChar, int keyCode);

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public ComponentHandler getHandler() {
		return handler;
	}

	public void setHandler(ComponentHandler handler) {
		this.handler = handler;
	}
}
