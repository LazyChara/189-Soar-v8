package com.soarclient.gui.mainmenu.component;

import com.soarclient.Soar;
import com.soarclient.animation.SimpleAnimation;
import com.soarclient.management.color.api.ColorPalette;
import com.soarclient.skia.Skia;
import com.soarclient.skia.font.Fonts;
import com.soarclient.ui.component.Component;
import com.soarclient.ui.component.api.PressAnimation;
import com.soarclient.ui.component.handler.impl.ButtonHandler;
import com.soarclient.utils.ColorUtils;
import com.soarclient.utils.language.I18n;
import com.soarclient.utils.mouse.MouseUtils;

public class MainMenuButton extends Component {

	private PressAnimation pressAnimation = new PressAnimation();
	private SimpleAnimation focusAnimation = new SimpleAnimation();
	private String title, icon;
	private int[] pressedPos;

	public MainMenuButton(String title, String icon, float x, float y, float width) {
		super(x, y);
		this.title = title;
		this.icon = icon;
		this.width = width;
		this.height = 40;
		pressedPos = new int[] { 0, 0 };
	}

	@Override
	public void draw(int mouseX, int mouseY) {

		ColorPalette palette = Soar.getInstance().getColorManager().getPalette();

		focusAnimation.onTick(MouseUtils.isInside(mouseX, mouseY, x, y, width, height) ? 0.08F : 0, 12);

		Skia.drawRoundedRect(x, y, width, height, 20, palette.getSurface());
		Skia.drawRoundedRect(x, y, width, height, 20,
				ColorUtils.applyAlpha(palette.getOnSurfaceVariant(), focusAnimation.getValue()));

		Skia.save();
		Skia.clip(x, y, width, height, 20);
		pressAnimation.draw(x + pressedPos[0], y + pressedPos[1], width, height, palette.getTertiaryContainer(), 1);
		Skia.restore();

		Skia.drawFullCenteredText(I18n.get(title), x + (width / 2), y + (height / 2), palette.getOnSurfaceVariant(),
				Fonts.getRegular(18));
		Skia.drawHeightCenteredText(icon, x + 12, y + (height / 2), palette.getOnSurfaceVariant(), Fonts.getIcon(20));
	}

	@Override
	public void mousePressed(int mouseX, int mouseY, int mouseButton) {

		if (MouseUtils.isInside(mouseX, mouseY, x, y, width, height) && mouseButton == 0) {
			pressedPos = new int[] { mouseX - (int) x, mouseY - (int) y };
			pressAnimation.mousePressed();
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

		if (MouseUtils.isInside(mouseX, mouseY, x, y, width, height) && mouseButton == 0) {
			if (handler instanceof ButtonHandler) {
				((ButtonHandler) handler).onAction();
			}
		}

		pressAnimation.mouseReleased();
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) {
	}
}
