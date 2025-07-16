package com.soarclient.gui.mainmenu.api;

import com.soarclient.gui.api.GuiTransition;
import com.soarclient.gui.api.page.PageGui;
import com.soarclient.gui.mainmenu.GuiSoarMainMenu;
import com.soarclient.ui.component.Component;

public abstract class PageGuiMainMenu extends PageGui {

	protected final GuiSoarMainMenu parent;

	public PageGuiMainMenu(GuiSoarMainMenu parent, GuiTransition transition) {
		super(transition, true, false);
		this.parent = parent;
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) {

		if (currentPage != null) {
			currentPage.keyTyped(typedChar, keyCode);
		}

		for (Component c : components) {
			c.keyTyped(typedChar, keyCode);
		}
	}
}
