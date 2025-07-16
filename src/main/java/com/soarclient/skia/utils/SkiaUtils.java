package com.soarclient.skia.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import io.github.humbleui.skija.Data;

public class SkiaUtils {

	public static Optional<byte[]> convertToBytes(String path) {
		try (InputStream inputStream = getResourceAsStream(path)) {
			return Optional.of(inputStream.readAllBytes());
		} catch (IOException e) {
			return Optional.empty();
		}
	}

	public static Optional<Data> convertToData(String path) {
		return convertToBytes(path).map(Data::makeFromBytes);
	}

	public static Optional<ImageInputStream> convertToImageInputStream(String resourceName) {
		try (InputStream stream = getResourceAsStream(resourceName)) {
			return Optional.of(ImageIO.createImageInputStream(stream));
		} catch (IOException e) {
			return Optional.empty();
		}
	}

	public static InputStream getResourceAsStream(String path) {
		InputStream inputStream = SkiaUtils.class.getResourceAsStream(path);
		if (inputStream == null) {
			throw new IllegalArgumentException("Resource not found: " + path);
		}
		return inputStream;
	}
}
