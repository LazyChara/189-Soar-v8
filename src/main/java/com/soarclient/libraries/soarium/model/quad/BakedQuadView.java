package com.soarclient.libraries.soarium.model.quad;

import com.soarclient.libraries.soarium.model.quad.properties.ModelQuadFacing;

public interface BakedQuadView extends ModelQuadView {
	ModelQuadFacing getNormalFace();

	int getFaceNormal();

	boolean hasShade();

	boolean hasAO();
}
