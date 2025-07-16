package com.soarclient.libraries.soarium.render.chunk.terrain.material;

import com.soarclient.libraries.soarium.render.chunk.terrain.TerrainRenderPass;
import com.soarclient.libraries.soarium.render.chunk.terrain.material.parameters.AlphaCutoffParameter;
import com.soarclient.libraries.soarium.render.chunk.terrain.material.parameters.MaterialParameters;

public class Material {
	public final TerrainRenderPass pass;
	public final int packed;

	public final AlphaCutoffParameter alphaCutoff;
	public final boolean mipped;

	public Material(TerrainRenderPass pass, AlphaCutoffParameter alphaCutoff, boolean mipped) {
		this.pass = pass;
		this.packed = MaterialParameters.pack(alphaCutoff, mipped);

		this.alphaCutoff = alphaCutoff;
		this.mipped = mipped;
	}

	public int bits() {
		return this.packed;
	}

	public boolean isTranslucent() {
		return this.pass.isTranslucent();
	}
}
