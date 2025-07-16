package com.soarclient.libraries.soarium.render.chunk.compile;

import java.util.Map;

import com.soarclient.libraries.soarium.render.chunk.RenderSection;
import com.soarclient.libraries.soarium.render.chunk.data.BuiltSectionInfo;
import com.soarclient.libraries.soarium.render.chunk.data.BuiltSectionMeshParts;
import com.soarclient.libraries.soarium.render.chunk.terrain.TerrainRenderPass;
import com.soarclient.libraries.soarium.render.chunk.translucent_sorting.data.TranslucentData;

/**
 * The result of a chunk rebuild task which contains any and all data that needs
 * to be processed or uploaded on the main thread. If a task is cancelled after
 * finishing its work and not before the result is processed, the result will
 * instead be discarded.
 */
public class ChunkBuildOutput extends ChunkSortOutput {
	public final BuiltSectionInfo info;
	public final TranslucentData translucentData;
	public final Map<TerrainRenderPass, BuiltSectionMeshParts> meshes;

	public ChunkBuildOutput(RenderSection render, int buildTime, TranslucentData translucentData, BuiltSectionInfo info,
			Map<TerrainRenderPass, BuiltSectionMeshParts> meshes) {
		super(render, buildTime);

		this.info = info;
		this.translucentData = translucentData;
		this.meshes = meshes;
	}

	public BuiltSectionMeshParts getMesh(TerrainRenderPass pass) {
		return this.meshes.get(pass);
	}

	@Override
	public void destroy() {
		super.destroy();

		for (BuiltSectionMeshParts data : this.meshes.values()) {
			data.getVertexData().free();
		}
	}
}
