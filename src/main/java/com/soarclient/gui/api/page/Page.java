package com.soarclient.gui.api.page;

import com.soarclient.animation.Animation;
import com.soarclient.animation.other.DummyAnimation;
import com.soarclient.gui.api.GuiTransition;

import net.minecraft.client.Minecraft;

public class Page {

	protected Minecraft mc = Minecraft.getMinecraft();

	protected float x, y, width, height;
	private String title, icon;
	protected PageGui parent;
	private Animation animation;
	private GuiTransition transition;

	public Page(PageGui parent, String title, String icon, GuiTransition transition) {
		this.parent = parent;
		this.title = title;
		this.icon = icon;
		this.transition = transition;
		this.x = 0;
		this.y = 0;
		this.width = 0;
		this.height = 0;
		this.animation = new DummyAnimation(1);
	}

	public void init() {
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

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public String getTitle() {
		return title;
	}

	public String getIcon() {
		return icon;
	}

	public GuiTransition getTransition() {
		return transition;
	}

	public void setTransition(GuiTransition transition) {
		this.transition = transition;
	}

	public Animation getAnimation() {
		return animation;
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
	}
}
