package com.soarclient.management.mod.impl.hud;

import java.util.ArrayList;

import org.lwjgl.input.Mouse;

import com.soarclient.event.EventBus;
import com.soarclient.event.impl.ClientTickEventListener;
import com.soarclient.event.impl.MouseClickEventListener;
import com.soarclient.event.impl.RenderSkiaEventListener;
import com.soarclient.management.mod.api.hud.SimpleHUDMod;
import com.soarclient.management.mod.settings.impl.BooleanSetting;
import com.soarclient.skia.Skia;
import com.soarclient.skia.font.Fonts;
import com.soarclient.skia.font.Icon;

import io.github.humbleui.skija.FontMetrics;
import io.github.humbleui.types.Rect;

public class CPSDisplayMod extends SimpleHUDMod
		implements RenderSkiaEventListener, MouseClickEventListener, ClientTickEventListener {

	private ArrayList<Long> leftPresses = new ArrayList<Long>();
	private ArrayList<Long> rightPresses = new ArrayList<Long>();
	private BooleanSetting rightClickSetting = new BooleanSetting("setting.rightclick",
			"setting.cps.rightclick.description", Icon.RIGHT_CLICK, this, true);

	public CPSDisplayMod() {
		super("mod.cpsdisplay.name", "mod.cpsdisplay.description", Icon.LEFT_CLICK);
	}

	@Override
	public void onRenderSkia(float partialTicks) {

		float fontSize = 9;
		float iconSize = 10.5F;
		float padding = 5;
		boolean hasIcon = getIcon() != null && iconSetting.isEnabled();
		Rect textBounds = Skia.getTextBounds(getText(), Fonts.getRegular(fontSize));
		Rect iconBounds = Skia.getTextBounds(getIcon(), Fonts.getIcon(iconSize));
		FontMetrics metrics = Fonts.getRegular(fontSize).getMetrics();
		float width = textBounds.getWidth() + (padding * 2) + (hasIcon ? iconBounds.getWidth() + 4 : 0);
		float height = fontSize + (padding * 2) - 1.5F;
		float textCenterY = (metrics.getAscent() - metrics.getDescent()) / 2 - metrics.getAscent();

		this.begin();
		this.drawBackground(getX(), getY(), width, height);

		if (hasIcon) {
			this.drawText(getIcon(), getX() + padding, getY() + (height / 2) - (iconBounds.getHeight() / 2),
					Fonts.getIcon(iconSize));
		}

		this.drawText(getText(), getX() + padding + (hasIcon ? iconBounds.getWidth() + 4 : 0),
				getY() + (height / 2) - textCenterY - 2, Fonts.getRegular(fontSize));
		this.finish();

		position.setSize(width, height);
	}

	@Override
	public void onClientTick() {
		leftPresses.removeIf(t -> System.currentTimeMillis() - t > 1000);
		rightPresses.removeIf(t -> System.currentTimeMillis() - t > 1000);
	}

	@Override
	public void onMouseClick(int button) {

		if (Mouse.getEventButtonState()) {

			if (button == 0) {
				leftPresses.add(System.currentTimeMillis());
			}

			if (button == 1) {
				rightPresses.add(System.currentTimeMillis());
			}
		}
	}

	@Override
	public void onEnable() {
		EventBus.getInstance().registers(this, ClientTickEvent.ID, MouseClickEvent.ID, RenderSkiaEvent.ID);
	}

	@Override
	public void onDisable() {
		EventBus.getInstance().unregisters(this, ClientTickEvent.ID, MouseClickEvent.ID, RenderSkiaEvent.ID);
	}

	@Override
	public String getText() {
		return (rightClickSetting.isEnabled() ? leftPresses.size() + " | " + rightPresses.size() : leftPresses.size())
				+ " CPS";
	}

	@Override
	public String getIcon() {
		return Icon.MOUSE;
	}
}
