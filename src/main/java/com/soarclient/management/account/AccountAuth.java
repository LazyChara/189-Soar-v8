package com.soarclient.management.account;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;

import com.soarclient.Soar;
import com.soarclient.libraries.skin.SkinHelper;
import com.soarclient.management.account.impl.MicrosoftAccount;
import com.soarclient.management.config.ConfigType;
import com.soarclient.utils.file.FileLocation;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.raphimc.minecraftauth.step.java.StepMCProfile.MCProfile;

public class AccountAuth {

	public static void refresh(Account account) {
		try {
			if (account instanceof MicrosoftAccount) {
				MicrosoftAccount msAccount = (MicrosoftAccount) account;
				MCProfile profile = msAccount.getMcProfile();

				msAccount.refresh();
				Minecraft.getMinecraft().setSession(new Session(profile.getName(), profile.getId().toString(),
						profile.getMcToken().getAccessToken(), "legacy"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Soar.getInstance().getConfigManager().save(ConfigType.ACCOUNT);
	}

	public static void login(Account account) {

		AccountManager accountManager = Soar.getInstance().getAccountManager();

		if (account instanceof MicrosoftAccount) {

			MicrosoftAccount msAccount = (MicrosoftAccount) account;
			MCProfile profile = msAccount.getMcProfile();

			accountManager.getAccounts().add(msAccount);
			SkinHelper.downloadJavaSkin(msAccount.getUUID().toString().replace("-", ""),
					new File(FileLocation.CACHE_DIR, msAccount.getUUID().toString().replace("-", "")));
			Minecraft.getMinecraft().setSession(new Session(profile.getName(), profile.getId().toString(),
					profile.getMcToken().getAccessToken(), "legacy"));
			accountManager.setCurrentAccount(msAccount);
			Soar.getInstance().getConfigManager().save(ConfigType.ACCOUNT);
		}
	}

	public static boolean openUrlWithDesktop(String url) {
		try {
			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				Desktop.getDesktop().browse(new URI(url));
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
