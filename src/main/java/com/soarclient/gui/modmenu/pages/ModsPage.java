package com.soarclient.gui.modmenu.pages;

import java.util.ArrayList;
import java.util.List;

import com.soarclient.Soar;
import com.soarclient.animation.SimpleAnimation;
import com.soarclient.gui.api.page.Page;
import com.soarclient.gui.api.page.PageGui;
import com.soarclient.gui.api.page.impl.LeftRightTransition;
import com.soarclient.gui.api.page.impl.RightLeftTransition;
import com.soarclient.management.color.api.ColorPalette;
import com.soarclient.management.mod.Mod;
import com.soarclient.skia.Skia;
import com.soarclient.skia.font.Fonts;
import com.soarclient.skia.font.Icon;
import com.soarclient.ui.component.api.PressAnimation;
import com.soarclient.ui.component.impl.text.SearchBar;
import com.soarclient.utils.ColorUtils;
import com.soarclient.utils.SearchUtils;
import com.soarclient.utils.language.I18n;
import com.soarclient.utils.mouse.MouseUtils;
import com.soarclient.utils.mouse.ScrollHelper;

public class ModsPage extends Page {

	private List<Item> items = new ArrayList<>();
	private ScrollHelper scrollHelper = new ScrollHelper();
	private SearchBar searchBar;

	public ModsPage(PageGui parent) {
		super(parent, "text.mods", Icon.INVENTORY_2, new RightLeftTransition(true));

		for (Mod m : Soar.getInstance().getModManager().getMods()) {

			Item i = new Item(m);

			if (m.isEnabled()) {
				i.pressAnimation.setPressed();
			}

			items.add(i);
		}
	}

	@Override
	public void init() {

		String text = "";

		if (searchBar != null) {
			text = searchBar.getText();
		}

		searchBar = new SearchBar(x + width - 260 - 32, y + 32, 260, text, () -> {
			scrollHelper.reset();
		});

		for (Item i : items) {
			i.xAnimation.setFirstTick(true);
			i.yAnimation.setFirstTick(true);
		}
	}

	@Override
	public void draw(int mouseX, int mouseY) {

		ColorPalette palette = Soar.getInstance().getColorManager().getPalette();

		int index = 0;
		float offsetX = 26;
		float offsetY = 0;

		scrollHelper.onScroll();
		mouseY = (int) (mouseY - scrollHelper.getValue());

		Skia.save();
		Skia.translate(0, scrollHelper.getValue());

		searchBar.draw(mouseX, mouseY);

		for (Item i : items) {

			Mod m = i.mod;
			SimpleAnimation focusAnimation = i.focusAnimation;
			SimpleAnimation xAnimation = i.xAnimation;
			SimpleAnimation yAnimation = i.yAnimation;

			if (m.isHidden()) {
				continue;
			}

			if (!searchBar.getText().isEmpty() && !SearchUtils.isSimilar(I18n.get(m.getName()), searchBar.getText())) {
				continue;
			}

			float itemX = x + offsetX;
			float itemY = y + 96 + offsetY;

			focusAnimation.onTick(
					MouseUtils.isInside(mouseX, mouseY, itemX, itemY, 244, 116 + 35) ? i.pressed ? 0.12F : 0.08F : 0,
					8);
			xAnimation.onTick(itemX, 14);
			yAnimation.onTick(itemY, 14);

			itemX = xAnimation.getValue();
			itemY = yAnimation.getValue();

			Skia.drawRoundedRectVarying(itemX, itemY, 244, 116, 26, 26, 0, 0, palette.getSurface());
			Skia.drawRoundedRectVarying(itemX, itemY + 116, 244, 35, 0, 0, 26, 26, palette.getSurfaceContainerLow());

			Skia.drawRoundedRectVarying(itemX, itemY + 116, 244, 35, 0, 0, 26, 26,
					ColorUtils.applyAlpha(palette.getSurfaceContainerLowest(), focusAnimation.getValue()));

			Skia.save();
			Skia.clip(itemX, itemY + 116, 244, 35, 0, 0, 26, 26);
			i.pressAnimation.draw(itemX + i.pressedPos[0], itemY + 116 + i.pressedPos[1], 224, 35,
					palette.getPrimaryContainer(), 1);
			Skia.restore();

			Skia.drawFullCenteredText(I18n.get(m.getName()), itemX + (244 / 2), itemY + 116 + (35 / 2),
					palette.getOnSurfaceVariant(), Fonts.getRegular(16));
			Skia.drawFullCenteredText(m.getIcon(), itemX + (244 / 2), itemY + (116 / 2), palette.getOnSurfaceVariant(),
					Fonts.getIcon(68));

			index++;
			offsetX += 32 + 244;

			if (index % 3 == 0) {
				offsetX = 26;
				offsetY += 22 + 151;
			}
		}

		Skia.restore();
	}

	@Override
	public void mousePressed(int mouseX, int mouseY, int mouseButton) {

		mouseY = (int) (mouseY - scrollHelper.getValue());

		searchBar.mousePressed(mouseX, mouseY, mouseButton);

		for (Item i : items) {

			float itemX = i.xAnimation.getValue();
			float itemY = i.yAnimation.getValue();

			if (i.mod.isHidden()) {
				continue;
			}

			if (!searchBar.getText().isEmpty()
					&& !SearchUtils.isSimilar(I18n.get(i.mod.getName()), searchBar.getText())) {
				continue;
			}

			if (mouseButton == 0) {

				if (MouseUtils.isInside(mouseX, mouseY, itemX, itemY + 116, 244, 35)) {
					i.pressed = true;
				}
			}

		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

		mouseY = (int) (mouseY - scrollHelper.getValue());

		searchBar.mousePressed(mouseX, mouseY, mouseButton);

		for (Item i : items) {

			Mod m = i.mod;
			float itemX = i.xAnimation.getValue();
			float itemY = i.yAnimation.getValue();

			if (i.mod.isHidden()) {
				continue;
			}

			if (!searchBar.getText().isEmpty()
					&& !SearchUtils.isSimilar(I18n.get(i.mod.getName()), searchBar.getText())) {
				continue;
			}

			if (mouseButton == 0) {

				if (MouseUtils.isInside(mouseX, mouseY, itemX, itemY + 116, 244, 35)) {
					i.pressedPos[0] = mouseX - itemX;
					i.pressedPos[1] = mouseY - (itemY + 116);
					m.toggle();

					if (m.isEnabled()) {
						i.pressAnimation.mousePressed();
					} else {
						i.pressAnimation.mouseReleased();
					}
				}

				if (MouseUtils.isInside(mouseX, mouseY, itemX, itemY, 244, 116)
						&& !Soar.getInstance().getModManager().getSettingsByMod(m).isEmpty()) {
					parent.setCurrentPage(new SettingsImplPage(parent, this.getClass(), m));
					this.setTransition(new LeftRightTransition(true));
				}
			}

			i.pressed = false;
		}
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) {
		searchBar.keyTyped(typedChar, keyCode);
	}

	@Override
	public void onClosed() {
		this.setTransition(new RightLeftTransition(true));
	}

	private class Item {

		private Mod mod;
		private SimpleAnimation focusAnimation = new SimpleAnimation();
		private SimpleAnimation xAnimation = new SimpleAnimation();
		private SimpleAnimation yAnimation = new SimpleAnimation();
		private PressAnimation pressAnimation = new PressAnimation();
		private float[] pressedPos = new float[] { 0, 0 };
		private boolean pressed;

		private Item(Mod mod) {
			this.mod = mod;
			this.pressed = false;
		}
	}
}
