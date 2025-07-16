package com.soarclient.gui.modmenu.pages;

import com.soarclient.gui.api.page.Page;
import com.soarclient.gui.api.page.PageGui;
import com.soarclient.gui.api.page.impl.RightLeftTransition;
import com.soarclient.skia.font.Icon;

public class HomePage extends Page {

	public HomePage(PageGui parent) {
		super(parent, "text.home", Icon.HOME, new RightLeftTransition(true));
	}
}
