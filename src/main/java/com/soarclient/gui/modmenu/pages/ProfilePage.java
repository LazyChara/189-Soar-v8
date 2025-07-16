package com.soarclient.gui.modmenu.pages;

import com.soarclient.gui.api.page.Page;
import com.soarclient.gui.api.page.PageGui;
import com.soarclient.gui.api.page.impl.RightLeftTransition;
import com.soarclient.skia.font.Icon;

public class ProfilePage extends Page {

	public ProfilePage(PageGui parent) {
		super(parent, "text.profile", Icon.DESCRIPTION, new RightLeftTransition(true));
	}
}
