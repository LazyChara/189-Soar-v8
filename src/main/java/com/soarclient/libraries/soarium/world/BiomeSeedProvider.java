package com.soarclient.libraries.soarium.world;

import net.minecraft.client.multiplayer.WorldClient;

public interface BiomeSeedProvider {
	static long getBiomeZoomSeed(WorldClient level) {
		return ((BiomeSeedProvider) level).sodium$getBiomeZoomSeed();
	}

	long sodium$getBiomeZoomSeed();
}
