package com.soarclient.libraries.skin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Base64;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.soarclient.utils.JsonUtils;
import com.soarclient.utils.network.HttpUtils;

public class SkinHelper {

	public static boolean downloadBedrockSkin(String xuid, File file) {

		JsonObject jsonObject = HttpUtils.get("https://api.geysermc.org/v2/skin/" + xuid);
		String textureId = JsonUtils.getStringProperty(jsonObject, "texture_id", null);

		if (textureId != null) {

			String url = "https://textures.minecraft.net/texture/" + textureId;

			try {
				downloadImage(url, file);
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
				return false;
			}

			return true;
		}

		return false;
	}

	public static boolean downloadJavaSkin(String uuid, File file) {

		JsonObject jsonObject = HttpUtils.get("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
		JsonArray propertiesArray = jsonObject.getAsJsonArray("properties");

		if (propertiesArray != null) {

			for (JsonElement element : propertiesArray) {

				JsonObject property = element.getAsJsonObject();
				String name = JsonUtils.getStringProperty(property, "name", null);

				if (name != null && name.equals("textures")) {

					String value = JsonUtils.getStringProperty(property, "value", null);

					if (value != null) {

						byte[] decodedBytes = Base64.getDecoder().decode(value);
						String decodedString = new String(decodedBytes);

						JsonObject decodedJsonObject = JsonParser.parseString(decodedString).getAsJsonObject();
						JsonObject texturesObject = JsonUtils.getObjectProperty(decodedJsonObject, "textures");

						if (texturesObject != null) {

							JsonObject skinObject = JsonUtils.getObjectProperty(texturesObject, "SKIN");
							String skinUrl = JsonUtils.getStringProperty(skinObject, "url", null);

							if (skinUrl != null) {
								try {
									downloadImage(skinUrl, file);
									return true;
								} catch (IOException | URISyntaxException e) {
									e.printStackTrace();
									return false;
								}
							}
						}

					}
				}
			}
		}

		return false;
	}

	private static void downloadImage(String imageUrl, File file) throws IOException, URISyntaxException {

		URL url = new URI(imageUrl).toURL();

		try (BufferedInputStream in = new BufferedInputStream(url.openStream());
				FileOutputStream out = new FileOutputStream(file)) {

			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = in.read(buffer, 0, 1024)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
		}
	}
}
