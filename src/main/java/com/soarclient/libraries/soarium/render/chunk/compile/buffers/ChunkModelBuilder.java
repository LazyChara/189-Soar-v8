package com.soarclient.libraries.soarium.render.chunk.compile.buffers;

import com.soarclient.libraries.soarium.model.quad.properties.ModelQuadFacing;
import com.soarclient.libraries.soarium.render.chunk.vertex.builder.ChunkMeshBufferBuilder;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public interface ChunkModelBuilder {
	ChunkMeshBufferBuilder getVertexBuffer(ModelQuadFacing facing);

	void addSprite(TextureAtlasSprite sprite);
}
