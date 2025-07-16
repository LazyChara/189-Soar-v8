package com.soarclient.libraries.soarium.render.viewport;

import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.joml.Vector3d;

import com.soarclient.libraries.soarium.compat.Mth;
import com.soarclient.libraries.soarium.compat.minecraft.math.SectionPos;
import com.soarclient.libraries.soarium.render.viewport.frustum.Frustum;

public final class Viewport {
	private final Frustum frustum;
	private final CameraTransform transform;

	private final SectionPos sectionCoords;
	private final BlockPos blockCoords;

	public Viewport(Frustum frustum, Vec3 position) {
		this.frustum = frustum;
		this.transform = new CameraTransform(position.xCoord, position.yCoord, position.zCoord);

		this.sectionCoords = SectionPos.of(SectionPos.posToSectionCoord(position.xCoord),
				SectionPos.posToSectionCoord(position.yCoord), SectionPos.posToSectionCoord(position.zCoord));

		this.blockCoords = new BlockPos(MathHelper.floor_double(position.xCoord),
				MathHelper.floor_double(position.yCoord), MathHelper.floor_double(position.zCoord));
	}

	public boolean isBoxVisible(int intOriginX, int intOriginY, int intOriginZ, float floatSizeX, float floatSizeY,
			float floatSizeZ) {
		float floatOriginX = intOriginX - this.transform.fracX;
		float floatOriginY = intOriginY - this.transform.fracY;
		float floatOriginZ = intOriginZ - this.transform.fracZ;

		return this.frustum.testAab(floatOriginX - floatSizeX, floatOriginY - floatSizeY, floatOriginZ - floatSizeZ,

				floatOriginX + floatSizeX, floatOriginY + floatSizeY, floatOriginZ + floatSizeZ);
	}

	public CameraTransform getTransform() {
		return this.transform;
	}

	public SectionPos getChunkCoord() {
		return this.sectionCoords;
	}

	public BlockPos getBlockCoord() {
		return this.blockCoords;
	}
}
