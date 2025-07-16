package com.soarclient.gui.modmenu.pages;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.soarclient.Soar;
import com.soarclient.gui.api.page.Page;
import com.soarclient.gui.api.page.PageGui;
import com.soarclient.gui.api.page.impl.RightTransition;
import com.soarclient.gui.modmenu.component.SettingBar;
import com.soarclient.management.mod.Mod;
import com.soarclient.management.mod.settings.Setting;
import com.soarclient.skia.Skia;
import com.soarclient.skia.font.Icon;
import com.soarclient.ui.component.impl.text.SearchBar;
import com.soarclient.utils.SearchUtils;
import com.soarclient.utils.language.I18n;
import com.soarclient.utils.mouse.ScrollHelper;

public class SettingsImplPage extends Page {

	private List<SettingBar> bars = new ArrayList<>();

	private Class<? extends Page> prevPage;
	private ScrollHelper scrollHelper = new ScrollHelper();
	private SearchBar searchBar;
	private Mod mod;

	public SettingsImplPage(PageGui parent, Class<? extends Page> prevPage, Mod mod) {
		super(parent, "text.mods", Icon.SETTINGS, new RightTransition(true));
		this.prevPage = prevPage;
		this.mod = mod;
	}

	@Override
	public void init() {

		bars.clear();

		String text = "";

		if (searchBar != null) {
			text = searchBar.getText();
		}

		searchBar = new SearchBar(x + width - 260 - 32, y + 32, 260, text, () -> {
			scrollHelper.reset();
		});

		for (Setting s : Soar.getInstance().getModManager().getSettingsByMod(mod)) {
			SettingBar bar = new SettingBar(s, x + 32, y + 32, width - 64);
			bars.add(bar);
		}

		parent.setClosable(false);
	}

	@Override
	public void draw(int mouseX, int mouseY) {

		float offsetY = 96;

		scrollHelper.onScroll();
		mouseY = (int) (mouseY - scrollHelper.getValue());

		Skia.save();
		Skia.translate(0, scrollHelper.getValue());

		searchBar.draw(mouseX, mouseY);

		for (SettingBar b : bars) {

			if (!searchBar.getText().isEmpty() && !SearchUtils.isSimilar(I18n.get(b.getTitle()), searchBar.getText())) {
				continue;
			}

			b.setY(y + offsetY);
			b.draw(mouseX, mouseY);

			offsetY += b.getHeight() + 18;
		}

		Skia.restore();
	}

	@Override
	public void mousePressed(int mouseX, int mouseY, int mouseButton) {

		mouseY = (int) (mouseY - scrollHelper.getValue());

		searchBar.mousePressed(mouseX, mouseY, mouseButton);

		for (SettingBar b : bars) {

			if (!searchBar.getText().isEmpty() && !SearchUtils.isSimilar(I18n.get(b.getTitle()), searchBar.getText())) {
				continue;
			}

			b.mousePressed(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

		mouseY = (int) (mouseY - scrollHelper.getValue());

		searchBar.mousePressed(mouseX, mouseY, mouseButton);

		for (SettingBar b : bars) {

			if (!searchBar.getText().isEmpty() && !SearchUtils.isSimilar(I18n.get(b.getTitle()), searchBar.getText())) {
				continue;
			}

			b.mouseReleased(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) {

		searchBar.keyTyped(typedChar, keyCode);

		for (SettingBar b : bars) {

			if (!searchBar.getText().isEmpty() && !SearchUtils.isSimilar(I18n.get(b.getTitle()), searchBar.getText())) {
				continue;
			}

			b.keyTyped(typedChar, keyCode);
		}

		if (keyCode == Keyboard.KEY_ESCAPE) {
			parent.setClosable(true);
			parent.setCurrentPage(prevPage);
		}
	}

	@Override
	public void onClosed() {
		if (!parent.isClosable()) {
			parent.setClosable(true);
			parent.getPage(prevPage).onClosed();
		}
	}
}
