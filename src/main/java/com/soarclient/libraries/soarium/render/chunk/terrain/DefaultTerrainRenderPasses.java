package com.soarclient.libraries.soarium.render.chunk.terrain;

import net.minecraft.util.EnumWorldBlockLayer;

public class DefaultTerrainRenderPasses {
	public static final TerrainRenderPass SOLID = new TerrainRenderPass(false, false);
	public static final TerrainRenderPass CUTOUT = new TerrainRenderPass(false, true);
	public static final TerrainRenderPass CUTOUT_MIPPED = new TerrainRenderPass(false, true);
	public static final TerrainRenderPass TRANSLUCENT = new TerrainRenderPass(true, false);

	public static final TerrainRenderPass[] ALL = new TerrainRenderPass[] { SOLID, CUTOUT, CUTOUT_MIPPED, TRANSLUCENT };

	public static TerrainRenderPass fromLayer(EnumWorldBlockLayer layer) {
		return switch (layer) {
		case SOLID -> SOLID;
		case CUTOUT -> CUTOUT;
		case CUTOUT_MIPPED -> CUTOUT_MIPPED;
		case TRANSLUCENT -> TRANSLUCENT;
		};
	}
}
