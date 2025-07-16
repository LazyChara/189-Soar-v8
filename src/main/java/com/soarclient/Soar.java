package com.soarclient;

import com.soarclient.animation.Delta;
import com.soarclient.event.EventBus;
import com.soarclient.event.impl.ClientTickEventListener.ClientTickEvent;
import com.soarclient.event.impl.GameLoopEventListener.GameLoopEvent;
import com.soarclient.gui.mainmenu.GuiSoarMainMenu;
import com.soarclient.libraries.soarium.Soarium;
import com.soarclient.management.account.AccountManager;
import com.soarclient.management.color.ColorManager;
import com.soarclient.management.config.ConfigManager;
import com.soarclient.management.config.ConfigType;
import com.soarclient.management.mod.ModManager;
import com.soarclient.management.music.MusicManager;
import com.soarclient.management.profile.ProfileManager;
import com.soarclient.utils.file.FileLocation;
import com.soarclient.utils.language.I18n;
import com.soarclient.utils.language.Language;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class Soar {

	private final static Soar instance = new Soar();
	private final String name, version;

	private ModManager modManager;
	private ColorManager colorManager;
	private MusicManager musicManager;
	private AccountManager accountManager;
	private ConfigManager configManager;
	private ProfileManager profileManager;

	private GuiScreen mainMenu;
	private long launchTime;

	public Soar() {
		name = "Soar";
		version = "8.0";
	}

	public void start() {

		I18n.setLanguage(Language.ENGLISH);
		FileLocation.init();
		Soarium.start();

		launchTime = System.currentTimeMillis();

		modManager = new ModManager();
		modManager.init();
		colorManager = new ColorManager();
		musicManager = new MusicManager();
		accountManager = new AccountManager();
		configManager = new ConfigManager();
		profileManager = new ProfileManager();

		EventBus.getInstance().register(new Delta(), GameLoopEvent.ID);
		EventBus.getInstance().register(new SoarListener(), ClientTickEvent.ID);
		mainMenu = new GuiSoarMainMenu().build();
		configManager.load(ConfigType.ACCOUNT);
		Minecraft.getMinecraft().fontRendererObj.drawString("", 0, 0, 0);
	}

	public void stop() {
		Soar.getInstance().getConfigManager().save(ConfigType.MOD);
	}

	public static Soar getInstance() {
		return instance;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public long getLaunchTime() {
		return launchTime;
	}

	public ModManager getModManager() {
		return modManager;
	}

	public ColorManager getColorManager() {
		return colorManager;
	}

	public MusicManager getMusicManager() {
		return musicManager;
	}

	public AccountManager getAccountManager() {
		return accountManager;
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}

	public ProfileManager getProfileManager() {
		return profileManager;
	}

	public GuiScreen getMainMenu() {
		return mainMenu;
	}
}
