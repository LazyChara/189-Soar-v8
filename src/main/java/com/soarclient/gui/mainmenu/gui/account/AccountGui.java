package com.soarclient.gui.mainmenu.gui.account;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.soarclient.gui.api.page.Page;
import com.soarclient.gui.api.page.impl.ZoomOutInTransition;
import com.soarclient.gui.mainmenu.GuiSoarMainMenu;
import com.soarclient.gui.mainmenu.api.PageGuiMainMenu;
import com.soarclient.gui.mainmenu.gui.MainGui;
import com.soarclient.gui.mainmenu.gui.account.pages.AccountListPage;
import com.soarclient.gui.mainmenu.gui.account.pages.WaitAuthPage;

public class AccountGui extends PageGuiMainMenu {

	public AccountGui(GuiSoarMainMenu parent) {
		super(parent, new ZoomOutInTransition(false));
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) {

		if (keyCode == Keyboard.KEY_ESCAPE) {
			parent.setCurrentGui(MainGui.class);
			return;
		}

		super.keyTyped(typedChar, keyCode);
	}

	@Override
	public List<Page> createPages() {

		List<Page> pages = new ArrayList<>();

		pages.add(new AccountListPage(this));
		pages.add(new WaitAuthPage(this));

		return pages;
	}

	@Override
	public float getX() {
		return (mc.displayWidth / 2) - (getWidth() / 2);
	}

	@Override
	public float getY() {
		return (mc.displayHeight / 2) - (getHeight() / 2);
	}

	@Override
	public float getWidth() {
		return 800;
	}

	@Override
	public float getHeight() {
		return 494;
	}
}
