package com.soarclient.management.color;

import com.soarclient.libraries.material3.hct.Hct;
import com.soarclient.management.color.api.ColorPalette;
import com.soarclient.management.mod.impl.settings.ModMenuSettings;

public class ColorManager {

	private ColorPalette palette;

	public ColorManager() {
		updatePalette();
	}

	public void onTick() {

		ModMenuSettings m = ModMenuSettings.getInstance();
		Hct hct = m.getHctColorSetting().getHct();

		if (palette == null) {
			updatePalette();
		}

		if (palette.isDarkMode() != m.getDarkModeSetting().isEnabled()) {
			updatePalette();
		}

		if (palette.getHct() != hct) {
			updatePalette();
		}
	}

	private void updatePalette() {

		ModMenuSettings m = ModMenuSettings.getInstance();

		palette = new ColorPalette(m.getHctColorSetting().getHct(), m.getDarkModeSetting().isEnabled());
	}

	public ColorPalette getPalette() {
		return palette;
	}
}
