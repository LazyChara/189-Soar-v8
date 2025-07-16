package com.soarclient.gui.mainmenu.gui.account.pages;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.soarclient.Soar;
import com.soarclient.animation.SimpleAnimation;
import com.soarclient.gui.api.page.Page;
import com.soarclient.gui.api.page.PageGui;
import com.soarclient.gui.api.page.impl.LeftTransition;
import com.soarclient.management.account.Account;
import com.soarclient.management.account.AccountAuth;
import com.soarclient.management.account.AccountManager;
import com.soarclient.management.color.api.ColorPalette;
import com.soarclient.skia.Skia;
import com.soarclient.skia.font.Fonts;
import com.soarclient.skia.font.Icon;
import com.soarclient.ui.component.handler.impl.ButtonHandler;
import com.soarclient.ui.component.impl.IconButton;
import com.soarclient.ui.component.impl.text.SearchBar;
import com.soarclient.utils.ColorUtils;
import com.soarclient.utils.Multithreading;
import com.soarclient.utils.SearchUtils;
import com.soarclient.utils.file.FileLocation;
import com.soarclient.utils.mouse.MouseUtils;
import com.soarclient.utils.mouse.ScrollHelper;

import io.github.humbleui.types.Rect;

public class AccountListPage extends Page {

	private List<Item> items = new ArrayList<>();
	private ScrollHelper scrollHelper = new ScrollHelper();
	private SearchBar searchBar;
	private IconButton addButton;
	private int lastSize;

	public AccountListPage(PageGui parent) {
		super(parent, "", "", new LeftTransition(true));

		List<Account> accounts = Soar.getInstance().getAccountManager().getAccounts();

		for (Account acc : accounts) {
			items.add(new Item(acc));
		}

		lastSize = accounts.size();
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

		addButton = new IconButton(Icon.ADD, 0, 0, IconButton.Size.LARGE, IconButton.Style.TERTIARY);
		addButton.setHandler(new ButtonHandler() {

			@Override
			public void onAction() {
				AccountListPage.this.parent.setCurrentPage(WaitAuthPage.class);
			}
		});

		addButton.setX(x + width - addButton.getWidth() - 22);
		addButton.setY(y + height - addButton.getHeight() - 22);

		for (Item i : items) {
			i.xAnimation.setFirstTick(true);
			i.yAnimation.setFirstTick(true);
		}
	}

	@Override
	public void draw(int mouseX, int mouseY) {

		Soar instance = Soar.getInstance();
		ColorPalette palette = instance.getColorManager().getPalette();
		AccountManager accountManager = instance.getAccountManager();

		if (accountManager.getAccounts().size() != lastSize) {
			lastSize = accountManager.getAccounts().size();
			items.clear();
			for (Account acc : accountManager.getAccounts()) {
				items.add(new Item(acc));
			}
		}

		float offsetX = 22;
		float offsetY = 88;

		scrollHelper.onScroll();
		mouseY = (int) (mouseY - scrollHelper.getValue());

		Skia.save();
		Skia.translate(0, scrollHelper.getValue());

		for (Item i : items) {

			Account acc = i.account;
			SimpleAnimation xAnimation = i.xAnimation;
			SimpleAnimation yAnimation = i.yAnimation;
			SimpleAnimation selectAnimation = i.selectAnimation;

			float itemX = x + offsetX;
			float itemY = y + offsetY;

			String uuid = acc.getUUID().toString();

			if (!searchBar.getText().isEmpty()
					&& !SearchUtils.isSimilar(acc.getDisplayString() + " " + uuid, searchBar.getText())) {
				continue;
			}

			xAnimation.onTick(itemX, 14);
			yAnimation.onTick(itemY, 14);

			itemX = xAnimation.getValue();
			itemY = yAnimation.getValue();

			Skia.drawRoundedRect(itemX, itemY, width - (22 * 2), 82, 20, palette.getSurface());
			Skia.drawPlayerHead(new File(FileLocation.CACHE_DIR, uuid.replace("-", "")), itemX + 12, itemY + 12, 58, 58,
					12);
			Skia.drawText(acc.getDisplayString(), itemX + 82, itemY + 24, palette.getOnSurface(), Fonts.getRegular(20));
			Skia.drawText(uuid, itemX + 82, itemY + 47, palette.getOnSurface(), Fonts.getRegular(15));

			selectAnimation.onTick(
					accountManager.getCurrentAccount() != null && accountManager.getCurrentAccount().equals(acc) ? 1
							: 0,
					10);

			Rect iconBounds = Skia.getTextBounds(Icon.CHECK, Fonts.getIconFill(30));

			Skia.drawHeightCenteredText(Icon.CHECK, itemX + (width - (22 * 2)) - iconBounds.getWidth() - 12,
					itemY + (82 / 2), ColorUtils.applyAlpha(palette.getPrimary(), selectAnimation.getValue()),
					Fonts.getIconFill(30));

			offsetY += 82 + 22;
		}

		searchBar.draw(mouseX, mouseY);

		Skia.restore();

		mouseY = (int) (mouseY + scrollHelper.getValue());
		addButton.draw(mouseX, mouseY);
	}

	@Override
	public void mousePressed(int mouseX, int mouseY, int mouseButton) {

		mouseY = (int) (mouseY - scrollHelper.getValue());
		searchBar.mousePressed(mouseX, mouseY, mouseButton);

		for (Item i : items) {

			SimpleAnimation xAnimation = i.xAnimation;
			SimpleAnimation yAnimation = i.yAnimation;
			float itemX = xAnimation.getValue();
			float itemY = yAnimation.getValue();

			if (MouseUtils.isInside(mouseX, mouseY, itemX, itemY, width - (22 * 2), 82) && mouseButton == 0) {

			}
		}

		mouseY = (int) (mouseY + scrollHelper.getValue());
		addButton.mousePressed(mouseX, mouseY, mouseButton);
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

		mouseY = (int) (mouseY - scrollHelper.getValue());
		searchBar.mouseReleased(mouseX, mouseY, mouseButton);

		for (Item i : items) {

			SimpleAnimation xAnimation = i.xAnimation;
			SimpleAnimation yAnimation = i.yAnimation;
			float itemX = xAnimation.getValue();
			float itemY = yAnimation.getValue();

			if (MouseUtils.isInside(mouseX, mouseY, itemX, itemY, width - (22 * 2), 82) && mouseButton == 0) {
				Multithreading.runAsync(() -> {
					AccountAuth.refresh(i.account);
					Soar.getInstance().getAccountManager().setCurrentAccount(i.account);
				});
			}
		}

		mouseY = (int) (mouseY + scrollHelper.getValue());
		addButton.mouseReleased(mouseX, mouseY, mouseButton);
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) {
		searchBar.keyTyped(typedChar, keyCode);
		super.keyTyped(typedChar, keyCode);
	}

	public void onClosed() {
	}

	private class Item {

		private Account account;
		private SimpleAnimation xAnimation = new SimpleAnimation();
		private SimpleAnimation yAnimation = new SimpleAnimation();
		private SimpleAnimation selectAnimation = new SimpleAnimation();

		private Item(Account account) {
			this.account = account;
		}
	}
}
