package com.soarclient.libraries.soarium.render.chunk.compile;

import com.soarclient.libraries.soarium.render.chunk.compile.pipeline.BlockRenderCache;
import com.soarclient.libraries.soarium.render.chunk.vertex.format.ChunkVertexType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;

public class ChunkBuildContext {
	public final ChunkBuildBuffers buffers;
	public final BlockRenderCache cache;

	public ChunkBuildContext(WorldClient level, ChunkVertexType vertexType) {
		this.buffers = new ChunkBuildBuffers(vertexType);
		this.cache = new BlockRenderCache(Minecraft.getMinecraft(), level);
	}

	public void cleanup() {
		this.buffers.destroy();
		this.cache.cleanup();
	}
}
