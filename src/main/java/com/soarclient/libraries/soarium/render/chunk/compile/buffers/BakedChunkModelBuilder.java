package com.soarclient.libraries.soarium.render.chunk.compile.buffers;

import com.soarclient.libraries.soarium.model.quad.properties.ModelQuadFacing;
import com.soarclient.libraries.soarium.render.chunk.data.BuiltSectionInfo;
import com.soarclient.libraries.soarium.render.chunk.vertex.builder.ChunkMeshBufferBuilder;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class BakedChunkModelBuilder implements ChunkModelBuilder {
	private final ChunkMeshBufferBuilder[] vertexBuffers;

	private BuiltSectionInfo.Builder renderData;

	public BakedChunkModelBuilder(ChunkMeshBufferBuilder[] vertexBuffers) {
		this.vertexBuffers = vertexBuffers;
	}

	@Override
	public ChunkMeshBufferBuilder getVertexBuffer(ModelQuadFacing facing) {
		return this.vertexBuffers[facing.ordinal()];
	}

	@Override
	public void addSprite(TextureAtlasSprite sprite) {
		this.renderData.addSprite(sprite);
	}

	public void destroy() {
		for (ChunkMeshBufferBuilder builder : this.vertexBuffers) {
			builder.destroy();
		}
	}

	public void begin(BuiltSectionInfo.Builder renderData, int sectionIndex) {
		this.renderData = renderData;

		for (var vertexBuffer : this.vertexBuffers) {
			vertexBuffer.start(sectionIndex);
		}
	}
}
