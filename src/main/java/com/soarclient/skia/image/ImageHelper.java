package com.soarclient.skia.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.lwjgl.opengl.GL11;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.soarclient.skia.context.SkiaContext;
import com.soarclient.skia.utils.SkiaUtils;

import io.github.humbleui.skija.ColorType;
import io.github.humbleui.skija.Image;
import io.github.humbleui.skija.SurfaceOrigin;

public class ImageHelper {

	private Map<String, Image> images = new HashMap<>();

	private Cache<Integer, Image> textures = Caffeine.newBuilder().expireAfterAccess(3, TimeUnit.MINUTES)
			.removalListener(new RemovalListener<Integer, Image>() {
				@Override
				public void onRemoval(Integer key, Image value, RemovalCause cause) {
					if (value != null) {
						value.close();
					}
				}
			}).build();

	public boolean load(int texture, float width, float height, SurfaceOrigin origin) {
		textures.get(texture, key -> {
			Image image = Image.adoptTextureFrom(SkiaContext.getContext(), texture, GL11.GL_TEXTURE_2D, (int) width,
					(int) height, GL11.GL_RGBA8, origin, ColorType.RGBA_8888);
			return image;
		});
		return true;
	}

	public boolean load(String filePath) {
		if (!images.containsKey(filePath)) {
			Optional<byte[]> encodedBytes = SkiaUtils.convertToBytes(filePath);
			if (encodedBytes.isPresent()) {
				images.put(filePath, Image.makeDeferredFromEncodedBytes(encodedBytes.get()));
				return true;
			} else {
				return false;
			}
		}
		return true;
	}

	public boolean load(File file) {

		if (!images.containsKey(file.getName())) {

			try {
				byte[] encoded = org.apache.commons.io.IOUtils.toByteArray(new FileInputStream(file));
				images.put(file.getName(), Image.makeDeferredFromEncodedBytes(encoded));
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		return true;
	}

	public Image get(String path) {

		if (images.containsKey(path)) {
			return images.get(path);
		}

		return null;
	}

	public Image get(int texture) {
		return textures.getIfPresent(texture);
	}
}
