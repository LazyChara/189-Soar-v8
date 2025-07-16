package com.soarclient.gui.mainmenu.api;

import com.soarclient.gui.api.GuiTransition;
import com.soarclient.gui.api.SoarGui;
import com.soarclient.gui.mainmenu.GuiSoarMainMenu;
import com.soarclient.ui.component.Component;

public abstract class SoarGuiMainMenu extends SoarGui {

	protected final GuiSoarMainMenu parent;

	public SoarGuiMainMenu(GuiSoarMainMenu parent, GuiTransition transition, boolean background) {
		super(transition, background, false);
		this.parent = parent;
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) {
		for (Component c : components) {
			c.keyTyped(typedChar, keyCode);
		}
	}
}
