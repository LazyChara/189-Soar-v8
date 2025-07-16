package com.soarclient.management.mod.impl.misc;

import java.util.Arrays;

import com.soarclient.management.mod.Mod;
import com.soarclient.management.mod.ModCategory;
import com.soarclient.management.mod.settings.impl.ComboSetting;
import com.soarclient.management.mod.settings.impl.NumberSetting;
import com.soarclient.skia.font.Icon;

public class WeatherChangerMod extends Mod {

	private static WeatherChangerMod instance;

	private ComboSetting weatherSetting = new ComboSetting("setting.weather", "setting.weather.description", Icon.CLOUD,
			this, Arrays.asList("setting.clear", "setting.rain", "setting.storm", "setting.snow"), "setting.clear");
	private NumberSetting rainIntensity = new NumberSetting("setting.rainintensity",
			"setting.rainintensity.description", Icon.RAINY, this, 1, 0, 1, 0.1F);
	private NumberSetting snowIntensity = new NumberSetting("setting.snowintensity",
			"setting.snowintensity.description", Icon.CLOUDY_SNOWING, this, 1, 0, 1, 0.1F);
	private NumberSetting thunderIntensity = new NumberSetting("setting.thunderintensity",
			"setting.thunderintensity.description", Icon.THUNDERSTORM, this, 1, 0, 1, 0.1F);

	public WeatherChangerMod() {
		super("mod.weatherchanger.name", "mod.weatherchanger.description", Icon.SUNNY, ModCategory.MISC);

		instance = this;
	}

	@Override
	public void onEnable() {
	}

	@Override
	public void onDisable() {
	}

	public static WeatherChangerMod getInstance() {
		return instance;
	}

	public boolean isRaining() {
		return !weatherSetting.getOption().equals("setting.clear");
	}

	public boolean isSnowing() {
		return weatherSetting.getOption().equals("setting.snow");
	}

	public boolean isThundering() {
		return weatherSetting.getOption().equals("setting.storm");
	}

	public NumberSetting getRainIntensity() {
		return rainIntensity;
	}

	public NumberSetting getSnowIntensity() {
		return snowIntensity;
	}

	public NumberSetting getThunderIntensity() {
		return thunderIntensity;
	}
}
