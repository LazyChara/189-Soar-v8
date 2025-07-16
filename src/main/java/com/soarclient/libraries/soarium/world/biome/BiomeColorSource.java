package com.soarclient.libraries.soarium.world.biome;

import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraft.world.biome.BiomeGenBase;

public enum BiomeColorSource {
	GRASS(BiomeGenBase::getGrassColorAtPos), FOLIAGE(BiomeGenBase::getFoliageColorAtPos),
	WATER((biome, pos) -> biome.waterColorMultiplier);

	private final BiomeColorHelper.ColorResolver provider;

	BiomeColorSource(BiomeColorHelper.ColorResolver provider) {
		this.provider = provider;
	}

	public BiomeColorHelper.ColorResolver getProvider() {
		return provider;
	}

	public static final BiomeColorSource[] VALUES = BiomeColorSource.values();
	public static final int COUNT = VALUES.length;
}
