package com.soarclient.libraries.soarium.render.chunk.shader;

import com.soarclient.libraries.soarium.gl.shader.ShaderConstants;
import com.soarclient.libraries.soarium.render.chunk.terrain.TerrainRenderPass;
import com.soarclient.libraries.soarium.render.chunk.vertex.format.ChunkVertexType;

public record ChunkShaderOptions(ChunkFogMode fog, TerrainRenderPass pass, ChunkVertexType vertexType) {
	public ShaderConstants constants() {
		ShaderConstants.Builder constants = ShaderConstants.builder();
		constants.addAll(this.fog.getDefines());

		if (this.pass.supportsFragmentDiscard()) {
			constants.add("USE_FRAGMENT_DISCARD");
		}

		constants.add("USE_VERTEX_COMPRESSION"); // TODO: allow compact vertex format to be disabled

		return constants.build();
	}
}
