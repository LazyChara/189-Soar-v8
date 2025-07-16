package com.soarclient.utils.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class HttpUtils {

	private static Gson gson = new Gson();

	public static JsonObject get(String url, Map<String, String> headers) {

		try {
			HttpURLConnection connection = setupConnection(url, "Mozilla/5.0", 5000, false);

			if (headers != null) {
				for (String header : headers.keySet()) {
					connection.addRequestProperty(header, headers.get(header));
				}
			}

			InputStream is = connection.getResponseCode() != 200 ? connection.getErrorStream()
					: connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

			return gson.fromJson(readResponse(rd), JsonObject.class);
		} catch (IOException e) {
		}

		return null;
	}

	public static JsonObject get(String url) {
		return get(url, null);
	}

	private static String readResponse(BufferedReader br) {

		try {
			StringBuilder sb = new StringBuilder();
			String line;

			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			return sb.toString();
		} catch (IOException e) {
		}

		return null;
	}

	private static HttpURLConnection setupConnection(String url, String userAgent, int timeout, boolean useCaches) {

		try {
			HttpURLConnection connection = ((HttpURLConnection) new URI(url).toURL().openConnection());

			connection.setRequestMethod("GET");
			connection.setUseCaches(useCaches);
			connection.addRequestProperty("User-Agent", userAgent);
			connection.setRequestProperty("Accept-Language", "en-US");
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			connection.setReadTimeout(timeout);
			connection.setConnectTimeout(timeout);
			connection.setDoOutput(true);

			return connection;
		} catch (Exception e) {
		}

		return null;
	}
}