package com.soarclient.management.config;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.soarclient.management.config.impl.AccountConfig;
import com.soarclient.management.config.impl.ModConfig;
import com.soarclient.utils.file.FileUtils;

public class ConfigManager {

	private final List<Config> configs = new ArrayList<>();

	public ConfigManager() {
		configs.add(new AccountConfig());
		configs.add(new ModConfig());
		load(ConfigType.MOD);
	}

	public void save(ConfigType type) {

		Config config = getConfig(type);
		Gson gson = new Gson();

		if (config == null || config.getFile() == null) {
			return;
		}

		FileUtils.createFile(config.getFile());

		try (FileWriter writer = new FileWriter(config.getFile())) {
			config.onSave();
			gson.toJson(config.getJsonObject(), writer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void load(ConfigType type) {

		Config config = getConfig(type);
		Gson gson = new Gson();

		if (config == null || config.getFile() == null || !config.getFile().exists()) {
			return;
		}

		try (FileReader reader = new FileReader(config.getFile())) {
			config.setJsonObject(gson.fromJson(reader, JsonObject.class));
			config.onLoad();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Config getConfig(ConfigType type) {
		return configs.stream().filter(config -> config.getType().equals(type)).findFirst().get();
	}
}