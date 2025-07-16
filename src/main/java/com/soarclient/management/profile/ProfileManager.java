package com.soarclient.management.profile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.soarclient.Soar;
import com.soarclient.management.config.Config;
import com.soarclient.management.config.ConfigManager;
import com.soarclient.management.config.ConfigType;
import com.soarclient.utils.JsonUtils;
import com.soarclient.utils.file.FileLocation;
import com.soarclient.utils.file.FileUtils;
import com.soarclient.utils.tuples.Pair;

public class ProfileManager {

	private List<Profile> profiles = new ArrayList<>();

	public ProfileManager() {
		readProfiles();
	}

	public void save(String name, String author, String serverIp, Object icon, ConfigType... types) {

		File zipFile = new File(FileLocation.PROFILE_DIR, name + ".zip");
		ConfigManager configManager = Soar.getInstance().getConfigManager();

		try (FileOutputStream fos = new FileOutputStream(zipFile); ZipOutputStream zipOut = new ZipOutputStream(fos)) {

			JsonObject jsonObject = new JsonObject();

			jsonObject.addProperty("name", name);
			jsonObject.addProperty("author", author);
			jsonObject.addProperty("serverIp", serverIp);

			if (icon instanceof ProfileIcon) {
				jsonObject.addProperty("icon", ((ProfileIcon) icon).getId());
			}

			addJsonToZip("info.json", jsonObject, zipOut);

			for (ConfigType t : types) {
				Config c = configManager.getConfig(t);
				c.onSave();
				addJsonToZip(t.getId() + ".josn", c.getJsonObject(), zipOut);
			}

			if (icon instanceof File) {

				File iconFile = (File) icon;

				addFileToZip("icon.png", iconFile, zipOut);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void load(Profile profile) {

		List<Pair<ConfigType, JsonObject>> configs = profile.getConfigs();

		for (Pair<ConfigType, JsonObject> p : configs) {

			Config config = Soar.getInstance().getConfigManager().getConfig(p.getFirst());

			config.setJsonObject(p.getSecond());
			config.onLoad();
		}
	}

	public void readProfiles() {

		for (File f : FileLocation.PROFILE_DIR.listFiles()) {

			if (f.getName().endsWith(".zip")) {

				List<Pair<ConfigType, JsonObject>> configs = new ArrayList<>();

				String name = "null";
				String author = "null";
				String serverIp = "";
				Object icon = null;

				try (FileInputStream fis = new FileInputStream(f); ZipInputStream zipIn = new ZipInputStream(fis)) {

					ZipEntry entry;
					while ((entry = zipIn.getNextEntry()) != null) {
						if (!entry.isDirectory()) {
							if (entry.getName().endsWith(".json")) {

								String jsonName = entry.getName().replace(".json", "");

								if (jsonName.equals("info")) {

									JsonObject infoJsonObject = readJsonFromZip(zipIn);

									name = JsonUtils.getStringProperty(infoJsonObject, "name", "null");
									author = JsonUtils.getStringProperty(infoJsonObject, "author", "null");
									icon = JsonUtils.getStringProperty(infoJsonObject, "icon", null);
									serverIp = JsonUtils.getStringProperty(infoJsonObject, "serverIp", "");

									continue;
								}

								ConfigType type = ConfigType.get(jsonName);

								if (type != null) {
									configs.add(Pair.of(type, readJsonFromZip(zipIn)));
								}
							} else if (entry.getName().endsWith(".png")) {
								try {
									File pngFile = new File(FileLocation.CACHE_DIR, FileUtils.getMd5Checksum(f));

									if (!pngFile.exists()) {
										saveFileFromZip(zipIn, pngFile);
									}

									icon = pngFile;
								} catch (NoSuchAlgorithmException e) {
									e.printStackTrace();
								}
							}
						}
						zipIn.closeEntry();
					}

					profiles.add(new Profile(name, author, configs, icon == null ? "" : icon, serverIp));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void addJsonToZip(String fileName, JsonObject jsonObject, ZipOutputStream zipOut) throws IOException {
		ZipEntry zipEntry = new ZipEntry(fileName);
		zipOut.putNextEntry(zipEntry);
		zipOut.write(jsonObject.toString().getBytes("UTF-8"));
		zipOut.closeEntry();
	}

	private void addFileToZip(String fileName, File file, ZipOutputStream zipOut) throws IOException {
		try (FileInputStream fis = new FileInputStream(file)) {
			ZipEntry zipEntry = new ZipEntry(fileName);
			zipOut.putNextEntry(zipEntry);

			byte[] bytes = new byte[1024];
			int length;
			while ((length = fis.read(bytes)) >= 0) {
				zipOut.write(bytes, 0, length);
			}
		}
	}

	private JsonObject readJsonFromZip(ZipInputStream zipIn) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		while ((len = zipIn.read(buffer)) > 0) {
			baos.write(buffer, 0, len);
		}
		String jsonString = baos.toString("UTF-8");
		return JsonParser.parseString(jsonString).getAsJsonObject();
	}

	private void saveFileFromZip(ZipInputStream zipIn, File file) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(file)) {
			byte[] buffer = new byte[1024];
			int len;
			while ((len = zipIn.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
		}
	}
}
