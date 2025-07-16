package com.soarclient.gui.mainmenu.gui;

import com.soarclient.Soar;
import com.soarclient.gui.api.page.impl.UpTransition;
import com.soarclient.gui.mainmenu.GuiSoarMainMenu;
import com.soarclient.gui.mainmenu.api.SoarGuiMainMenu;
import com.soarclient.gui.mainmenu.component.MainMenuButton;
import com.soarclient.gui.mainmenu.gui.account.AccountGui;
import com.soarclient.skia.Skia;
import com.soarclient.skia.font.Icon;
import com.soarclient.ui.component.handler.impl.ButtonHandler;

import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiSelectWorld;

public class MainGui extends SoarGuiMainMenu {

	public MainGui(GuiSoarMainMenu parent) {
		super(parent, new UpTransition(false), false);
	}

	@Override
	public void init() {

		components.clear();

		int y = mc.displayHeight / 4 + 112;
		int space = 48;

		MainMenuButton singleplayer = new MainMenuButton("mainmenu.singleplayer", Icon.PERSON,
				(mc.displayWidth / 2) - 200, y, 400);
		MainMenuButton multiplayer = new MainMenuButton("mainmenu.multiplayer", Icon.GROUPS,
				(mc.displayWidth / 2) - 200, y + space, 400);
		MainMenuButton accountManager = new MainMenuButton("mainmenu.accountmanager", Icon.ACCOUNT_CIRCLE,
				(mc.displayWidth / 2) - 200, y + (space * 2), 400);
		MainMenuButton settings = new MainMenuButton("mainmenu.settings", Icon.SETTINGS, (mc.displayWidth / 2) - 200,
				y + (space * 3), 196);
		MainMenuButton exit = new MainMenuButton("mainmanu.exit", Icon.EXIT_TO_APP, (mc.displayWidth / 2) + 2,
				y + (space * 3), 196);

		singleplayer.setHandler(new ButtonHandler() {

			@Override
			public void onAction() {
				mc.displayGuiScreen(new GuiSelectWorld(Soar.getInstance().getMainMenu()));
			}
		});

		multiplayer.setHandler(new ButtonHandler() {

			@Override
			public void onAction() {
				mc.displayGuiScreen(new GuiMultiplayer(Soar.getInstance().getMainMenu()));
			}
		});

		accountManager.setHandler(new ButtonHandler() {

			@Override
			public void onAction() {
				parent.setCurrentGui(AccountGui.class);
			}
		});

		settings.setHandler(new ButtonHandler() {

			@Override
			public void onAction() {
				mc.displayGuiScreen(new GuiOptions(Soar.getInstance().getMainMenu(), mc.gameSettings));
			}
		});

		exit.setHandler(new ButtonHandler() {

			@Override
			public void onAction() {
				mc.shutdown();
			}
		});

		components.add(singleplayer);
		components.add(multiplayer);
		components.add(accountManager);
		components.add(settings);
		components.add(exit);
		super.init();
	}

	@Override
	public void drawSkia(int mouseX, int mouseY) {

		float size = 152;

		Skia.drawImage("logo.png", (mc.displayWidth / 2) - (size / 2), mc.displayHeight / 4 + (112) - size - 4, size,
				size);
	}

	@Override
	public float getX() {
		return 0;
	}

	@Override
	public float getY() {
		return 0;
	}

	@Override
	public float getWidth() {
		return mc.displayWidth;
	}

	@Override
	public float getHeight() {
		return mc.displayHeight;
	}
}
