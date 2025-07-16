package com.soarclient.libraries.soarium.gui.locale;

public class SoariumI18n {

	public static String get(String key) {
		if (key.startsWith("soarium")) {
			return com.soarclient.utils.language.I18n.get(key);
		} else {
			return net.minecraft.client.resources.I18n.format(key);
		}
	}
}
