package com.soarclient.libraries.patches.font.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.soarclient.libraries.patches.font.Enhancement;
import com.soarclient.libraries.patches.font.hash.impl.StringHash;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.renderer.GLAllocation;

public final class EnhancedFontRenderer implements Enhancement {

	private static final List<EnhancedFontRenderer> instances = new ArrayList<>();
	private final List<StringHash> obfuscated = new ArrayList<>();
	private final Object2IntMap<String> stringWidthCache = new Object2IntOpenHashMap<>(5000);
	private final Queue<Integer> glRemoval = new ConcurrentLinkedQueue<>();
	private final Cache<StringHash, CachedString> stringCache = Caffeine.newBuilder()
			.removalListener((key, value, cause) -> {
				if (value == null)
					return;
				glRemoval.add(((CachedString) value).getListId());
			}).executor(POOL).maximumSize(5000).build();

	public EnhancedFontRenderer() {
		instances.add(this);
	}

	public static List<EnhancedFontRenderer> getInstances() {
		return instances;
	}

	@Override
	public String getName() {
		return "Enhanced Font Renderer";
	}

	@Override
	public void tick() {
		stringCache.invalidateAll(obfuscated);
		obfuscated.clear();
	}

	public int getGlList() {
		final Integer poll = glRemoval.poll();
		return poll == null ? GLAllocation.generateDisplayLists(1) : poll;
	}

	public CachedString get(StringHash key) {
		return stringCache.getIfPresent(key);
	}

	public void cache(StringHash key, CachedString value) {
		stringCache.put(key, value);
	}

	public Object2IntMap<String> getStringWidthCache() {
		return stringWidthCache;
	}

	public void invalidateAll() {
		this.stringCache.invalidateAll();
	}

	public List<StringHash> getObfuscated() {
		return obfuscated;
	}
}