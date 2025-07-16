package com.soarclient.management.config.impl;

import java.util.List;

import com.google.gson.JsonObject;
import com.soarclient.Soar;
import com.soarclient.libraries.material3.hct.Hct;
import com.soarclient.management.config.Config;
import com.soarclient.management.config.ConfigType;
import com.soarclient.management.mod.Mod;
import com.soarclient.management.mod.ModManager;
import com.soarclient.management.mod.api.AnchorPosition;
import com.soarclient.management.mod.api.Position;
import com.soarclient.management.mod.api.hud.HUDMod;
import com.soarclient.management.mod.settings.Setting;
import com.soarclient.management.mod.settings.impl.BooleanSetting;
import com.soarclient.management.mod.settings.impl.ColorSetting;
import com.soarclient.management.mod.settings.impl.ComboSetting;
import com.soarclient.management.mod.settings.impl.HctColorSetting;
import com.soarclient.management.mod.settings.impl.KeybindSetting;
import com.soarclient.management.mod.settings.impl.NumberSetting;
import com.soarclient.management.mod.settings.impl.StringSetting;
import com.soarclient.utils.ColorUtils;
import com.soarclient.utils.JsonUtils;

public class ModConfig extends Config {

	public ModConfig() {
		super(ConfigType.MOD);
	}

	@Override
	public void onLoad() {

		ModManager modManager = Soar.getInstance().getModManager();
		JsonObject modJsonObject = JsonUtils.getObjectProperty(jsonObject, "mods");

		for (Mod m : modManager.getMods()) {
			m.setEnabled(false);
		}

		if (modJsonObject != null) {

			for (Mod m : modManager.getMods()) {

				JsonObject modImplJsonObject = JsonUtils.getObjectProperty(modJsonObject, m.getName());

				if (modImplJsonObject != null) {

					m.setEnabled(JsonUtils.getBooleanProperty(modImplJsonObject, "enabled", false));

					if (m instanceof HUDMod) {

						HUDMod hudMod = (HUDMod) m;
						Position position = hudMod.getPosition();

						position.setAnchor(
								AnchorPosition.get(JsonUtils.getIntProperty(modImplJsonObject, "anchor", 0)));
						position.setScale(JsonUtils.getFloatProperty(modImplJsonObject, "scale", 1));
						position.setRawPosition(JsonUtils.getFloatProperty(modImplJsonObject, "x", 0),
								JsonUtils.getFloatProperty(modImplJsonObject, "y", 0));
					}

					List<Setting> settings = modManager.getSettingsByMod(m);

					if (!settings.isEmpty()) {

						JsonObject settingJsonObject = JsonUtils.getObjectProperty(modImplJsonObject, "settings");

						if (settingJsonObject != null) {

							for (Setting s : settings) {

								if (s instanceof BooleanSetting) {

									BooleanSetting bs = (BooleanSetting) s;

									bs.setEnabled(JsonUtils.getBooleanProperty(settingJsonObject, s.getName(),
											bs.getDefaultValue()));
								}

								if (s instanceof ColorSetting) {

									ColorSetting cs = (ColorSetting) s;

									cs.setColor(ColorUtils.getColorFromInt(JsonUtils.getIntProperty(settingJsonObject,
											s.getName(), cs.getColor().getRGB())));
								}

								if (s instanceof ComboSetting) {

									ComboSetting cs = (ComboSetting) s;
									String option = JsonUtils.getStringProperty(settingJsonObject, s.getName(), "");

									cs.setOption(cs.has(option) ? option : cs.getDefaultOption());
								}

								if (s instanceof HctColorSetting) {

									HctColorSetting hcs = (HctColorSetting) s;

									hcs.setHct(Hct.fromInt(JsonUtils.getIntProperty(settingJsonObject, s.getName(),
											hcs.getDefaultHct().toInt())));
								}

								if (s instanceof KeybindSetting) {

									KeybindSetting ks = (KeybindSetting) s;

									ks.setKeyCode(JsonUtils.getIntProperty(settingJsonObject, s.getName(),
											ks.getDefaultKeyCode()));
								}

								if (s instanceof NumberSetting) {

									NumberSetting ns = (NumberSetting) s;

									ns.setValue(JsonUtils.getFloatProperty(settingJsonObject, s.getName(),
											ns.getDefaultValue()));
								}

								if (s instanceof StringSetting) {

									StringSetting ss = (StringSetting) s;

									ss.setValue(JsonUtils.getStringProperty(settingJsonObject, s.getName(),
											ss.getDefaultValue()));
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void onSave() {

		ModManager modManager = Soar.getInstance().getModManager();
		JsonObject modJsonObject = new JsonObject();

		for (Mod m : modManager.getMods()) {

			JsonObject modImplJsonObject = new JsonObject();

			modImplJsonObject.addProperty("enabled", m.isEnabled());

			if (m instanceof HUDMod) {

				HUDMod hudMod = (HUDMod) m;
				Position position = hudMod.getPosition();

				modImplJsonObject.addProperty("x", position.getRawX());
				modImplJsonObject.addProperty("y", position.getRawY());
				modImplJsonObject.addProperty("scale", position.getScale());
				modImplJsonObject.addProperty("anchor", position.getAnchor().getId());
			}

			modJsonObject.add(m.getName(), modImplJsonObject);

			List<Setting> settings = modManager.getSettingsByMod(m);

			if (!settings.isEmpty()) {

				JsonObject settingJsonObject = new JsonObject();

				for (Setting s : settings) {

					if (s instanceof BooleanSetting) {
						settingJsonObject.addProperty(s.getName(), ((BooleanSetting) s).isEnabled());
					}

					if (s instanceof ColorSetting) {
						settingJsonObject.addProperty(s.getName(), ((ColorSetting) s).getColor().getRGB());
					}

					if (s instanceof ComboSetting) {
						settingJsonObject.addProperty(s.getName(), ((ComboSetting) s).getOption());
					}

					if (s instanceof HctColorSetting) {
						settingJsonObject.addProperty(s.getName(), ((HctColorSetting) s).getHct().toInt());
					}

					if (s instanceof KeybindSetting) {
						settingJsonObject.addProperty(s.getName(), ((KeybindSetting) s).getKeyCode());
					}

					if (s instanceof NumberSetting) {
						settingJsonObject.addProperty(s.getName(), ((NumberSetting) s).getValue());
					}

					if (s instanceof StringSetting) {
						settingJsonObject.addProperty(s.getName(), ((StringSetting) s).getValue());
					}
				}

				modImplJsonObject.add("settings", settingJsonObject);
			}
		}

		jsonObject.add("mods", modJsonObject);
	}
}