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
import com.soarclient.ui.component.impl.text.SearchBar;
import com.soarclient.utils.SearchUtils;
import com.soarclient.utils.language.I18n;
import com.soarclient.utils.mouse.MouseUtils;
import com.soarclient.utils.mouse.ScrollHelper;

public class SettingsPage extends Page {

	private List<Item> items = new ArrayList<>();
	private ScrollHelper scrollHelper = new ScrollHelper();
	private SearchBar searchBar;

	public SettingsPage(PageGui parent) {
		super(parent, "text.settings", Icon.SETTINGS, new RightLeftTransition(true));

		for (Mod m : Soar.getInstance().getModManager().getMods()) {
			if (m.isHidden()) {
				items.add(new Item(m));
			}
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
	}

	@Override
	public void draw(int mouseX, int mouseY) {

		ColorPalette palette = Soar.getInstance().getColorManager().getPalette();

		float offsetY = 96;

		scrollHelper.onScroll();
		mouseY = (int) (mouseY - scrollHelper.getValue());

		Skia.save();
		Skia.translate(0, scrollHelper.getValue());

		searchBar.draw(mouseX, mouseY);

		for (Item i : items) {

			SimpleAnimation yAnimation = i.yAnimation;
			Mod m = i.mod;

			if (!searchBar.getText().isEmpty() && !SearchUtils
					.isSimilar(I18n.get(m.getName()) + " " + I18n.get(m.getDescription()), searchBar.getText())) {
				continue;
			}

			float itemY = y + offsetY;

			yAnimation.onTick(itemY, 14);

			itemY = yAnimation.getValue();

			Skia.drawRoundedRect(x + 32, itemY, width - 64, 68, 18, palette.getSurface());
			Skia.drawFullCenteredText(m.getIcon(), x + 32 + 30, itemY + (68 / 2), palette.getOnSurface(),
					Fonts.getIcon(32));
			Skia.drawText(I18n.get(m.getName()), x + 32 + 52, itemY + 20, palette.getOnSurface(), Fonts.getRegular(17));
			Skia.drawText(I18n.get(m.getDescription()), x + 32 + 52, itemY + 37, palette.getOnSurfaceVariant(),
					Fonts.getRegular(14));
			Skia.drawHeightCenteredText(">", x + width - 54, itemY + (68 / 2), palette.getOnSurface(),
					Fonts.getRegular(20));

			offsetY += 68 + 18;
		}

		Skia.restore();
	}

	@Override
	public void mousePressed(int mouseX, int mouseY, int mouseButton) {

		mouseY = (int) (mouseY - scrollHelper.getValue());

		searchBar.mousePressed(mouseX, mouseY, mouseButton);
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

		mouseY = (int) (mouseY - scrollHelper.getValue());

		searchBar.mouseReleased(mouseX, mouseY, mouseButton);

		for (Item i : items) {

			float itemY = i.yAnimation.getValue();
			Mod m = i.mod;

			if (!searchBar.getText().isEmpty() && !SearchUtils
					.isSimilar(I18n.get(m.getName()) + " " + I18n.get(m.getDescription()), searchBar.getText())) {
				continue;
			}

			if (MouseUtils.isInside(mouseX, mouseY, x + 32, itemY, width - 64, 68) && mouseButton == 0) {
				parent.setCurrentPage(new SettingsImplPage(parent, this.getClass(), m));
				this.setTransition(new LeftRightTransition(true));
			}
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

		private SimpleAnimation yAnimation = new SimpleAnimation();
		private Mod mod;

		private Item(Mod mod) {
			this.mod = mod;
		}
	}
}
