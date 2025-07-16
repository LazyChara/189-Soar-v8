package com.soarclient.libraries.soarium.render.viewport.frustum;

import net.minecraft.client.renderer.culling.Frustum;

public final class SimpleFrustum implements com.soarclient.libraries.soarium.render.viewport.frustum.Frustum {
	private final Frustum frustum;

	public SimpleFrustum(Frustum frustumIntersection) {
		this.frustum = frustumIntersection;
	}

	@Override
	public boolean testAab(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		return frustum.isBoxInFrustum(minX, minY, minZ, maxX, maxY, maxZ);
	}
}
