package com.soarclient.gui.modmenu;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.soarclient.Soar;
import com.soarclient.animation.Animation;
import com.soarclient.animation.Duration;
import com.soarclient.animation.SimpleAnimation;
import com.soarclient.animation.cubicbezier.impl.EaseStandard;
import com.soarclient.animation.other.DummyAnimation;
import com.soarclient.gui.api.page.Page;
import com.soarclient.gui.edithud.GuiEditHUD;
import com.soarclient.management.color.api.ColorPalette;
import com.soarclient.skia.Skia;
import com.soarclient.skia.font.Fonts;
import com.soarclient.skia.font.Icon;
import com.soarclient.ui.component.Component;
import com.soarclient.ui.component.handler.impl.ButtonHandler;
import com.soarclient.ui.component.impl.IconButton;
import com.soarclient.utils.ColorUtils;
import com.soarclient.utils.language.I18n;
import com.soarclient.utils.mouse.MouseUtils;

import io.github.humbleui.skija.Font;
import io.github.humbleui.types.Rect;

public class NavigationRail extends Component {

	private GuiModMenu parent;

	private List<Navigation> navigations = new ArrayList<>();
	private Navigation currentNavigation;
	private IconButton editButton;

	public NavigationRail(GuiModMenu parent, float x, float y, float width, float height) {
		super(x, y);
		this.parent = parent;
		this.width = width;
		this.height = height;

		for (Page p : parent.getPages()) {

			Navigation n = new Navigation(p);

			if (p.getTitle().equals(parent.getCurrentPage().getTitle())) {
				currentNavigation = n;
				n.animation = new EaseStandard(Duration.MEDIUM_3, 0, 1);
			}

			navigations.add(n);
		}

		editButton = new IconButton(Icon.EDIT, x, y + 44, IconButton.Size.NORMAL, IconButton.Style.TERTIARY);
		editButton.setX(x + (width / 2) - (editButton.getWidth() / 2));
		editButton.setHandler(new ButtonHandler() {

			@Override
			public void onAction() {
				parent.close(new GuiEditHUD(mc.currentScreen).build());
			}
		});
	}

	@Override
	public void draw(int mouseX, int mouseY) {

		ColorPalette palette = Soar.getInstance().getColorManager().getPalette();

		Skia.drawRoundedRectVarying(x, y, width, height, 35, 0, 0, 35, palette.getSurface());

		editButton.draw(mouseX, mouseY);

		float offsetY = 140;

		for (Navigation n : navigations) {

			Page p = n.page;
			String title = p.getTitle();
			String icon = p.getIcon();
			Font font = currentNavigation.equals(n) ? Fonts.getIconFill(24) : Fonts.getIcon(24);
			Rect bounds = Skia.getTextBounds(icon, font);
			float iconWidth = bounds.getWidth();
			float iconHeight = bounds.getHeight();

			Color c0 = currentNavigation.equals(n) ? palette.getOnSecondaryContainer() : palette.getOnSurfaceVariant();
			Color c1 = currentNavigation.equals(n) ? palette.getOnSurface() : palette.getOnSurfaceVariant();

			Animation animation = n.animation;
			float selWidth = 56;
			float selHeight = 32;
			boolean focus = MouseUtils.isInside(mouseX, mouseY, x + (width / 2) - (selWidth / 2), y + offsetY, selWidth,
					selHeight) || n.pressed;

			n.focusAnimation.onTick(focus ? n.pressed ? 0.12F : 0.08F : 0, 8);

			Skia.drawRoundedRect(x + (width / 2) - (selWidth / 2), y + offsetY, selWidth, selHeight, 16,
					ColorUtils.applyAlpha(palette.getOnSurfaceVariant(), n.focusAnimation.getValue()));

			if (animation.getEnd() != 0 || !animation.isFinished()) {
				Skia.drawRoundedRect(
						x + (width / 2) - (selWidth / 2) + (selWidth - selWidth * animation.getValue()) / 2,
						y + offsetY, selWidth * animation.getValue(), selHeight, 16,
						ColorUtils.applyAlpha(palette.getSecondaryContainer(), animation.getValue()));
			}

			Skia.drawText(icon, x + (width / 2) - (iconWidth / 2), y + (offsetY + (selHeight / 2)) - (iconHeight / 2),
					c0, font);
			Skia.drawCenteredText(I18n.get(title), x + (width / 2), y + offsetY + selHeight + 5, c1,
					Fonts.getMedium(12));

			offsetY += 68;
		}
	}

	@Override
	public void mousePressed(int mouseX, int mouseY, int mouseButton) {

		editButton.mousePressed(mouseX, mouseY, mouseButton);

		float offsetY = 140;
		float selWidth = 56;
		float selHeight = 32;

		for (Navigation n : navigations) {

			if (MouseUtils.isInside(mouseX, mouseY, x + (width / 2) - (selWidth / 2), y + offsetY, selWidth, selHeight)
					&& mouseButton == 0 && !currentNavigation.equals(n)) {
				n.pressed = true;
			}

			offsetY += 68;
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

		editButton.mouseReleased(mouseX, mouseY, mouseButton);

		float offsetY = 140;
		float selWidth = 56;
		float selHeight = 32;

		for (Navigation n : navigations) {
			if (MouseUtils.isInside(mouseX, mouseY, x + (width / 2) - (selWidth / 2), y + offsetY, selWidth, selHeight)
					&& mouseButton == 0 && !currentNavigation.equals(n)) {
				currentNavigation.animation = new EaseStandard(Duration.MEDIUM_3, 1, 0);
				currentNavigation = n;
				parent.setCurrentPage(n.page);
				currentNavigation.animation = new EaseStandard(Duration.MEDIUM_3, 0, 1);
			}

			n.pressed = false;
			offsetY += 68;
		}
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) {
	}

	private class Navigation {

		private SimpleAnimation focusAnimation = new SimpleAnimation();

		private Animation animation;
		private Page page;
		private boolean pressed;

		private Navigation(Page page) {
			this.page = page;
			this.animation = new DummyAnimation();
			this.pressed = false;
		}
	}
}
