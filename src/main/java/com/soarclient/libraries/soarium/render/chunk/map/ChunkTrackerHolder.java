package com.soarclient.libraries.soarium.render.chunk.map;

import net.minecraft.client.multiplayer.WorldClient;

public interface ChunkTrackerHolder {
	static ChunkTracker get(WorldClient level) {
		return ((ChunkTrackerHolder) level).sodium$getTracker();
	}

	ChunkTracker sodium$getTracker();
}
