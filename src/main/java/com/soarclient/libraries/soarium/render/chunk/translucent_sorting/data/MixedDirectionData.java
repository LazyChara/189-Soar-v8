package com.soarclient.libraries.soarium.render.chunk.translucent_sorting.data;

import com.soarclient.libraries.soarium.compat.minecraft.math.SectionPos;
import com.soarclient.libraries.soarium.model.quad.properties.ModelQuadFacing;

public abstract class MixedDirectionData extends PresentTranslucentData {
	private final int[] vertexCounts = new int[ModelQuadFacing.COUNT];

	MixedDirectionData(SectionPos sectionPos, int vertexCount, int quadCount) {
		super(sectionPos, quadCount);
		this.vertexCounts[ModelQuadFacing.UNASSIGNED.ordinal()] = vertexCount;
	}

	@Override
	public int[] getVertexCounts() {
		return this.vertexCounts;
	}
}
