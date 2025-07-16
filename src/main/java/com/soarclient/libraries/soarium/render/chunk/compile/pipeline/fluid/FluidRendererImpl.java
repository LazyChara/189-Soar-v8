package com.soarclient.libraries.soarium.render.chunk.compile.pipeline.fluid;

import com.soarclient.libraries.soarium.compat.minecraft.WorldUtil;
import com.soarclient.libraries.soarium.model.color.ColorProvider;
import com.soarclient.libraries.soarium.model.color.ColorProviderRegistry;
import com.soarclient.libraries.soarium.model.light.LightPipelineProvider;
import com.soarclient.libraries.soarium.render.chunk.compile.ChunkBuildBuffers;
import com.soarclient.libraries.soarium.render.chunk.compile.buffers.ChunkModelBuilder;
import com.soarclient.libraries.soarium.render.chunk.terrain.material.DefaultMaterials;
import com.soarclient.libraries.soarium.render.chunk.terrain.material.Material;
import com.soarclient.libraries.soarium.render.chunk.translucent_sorting.TranslucentGeometryCollector;
import com.soarclient.libraries.soarium.util.FluidSprites;
import com.soarclient.libraries.soarium.world.LevelSlice;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

public class FluidRendererImpl extends FluidRenderer {
	private final ColorProviderRegistry colorProviderRegistry;
	private final DefaultFluidRenderer defaultRenderer;

	private final FluidSprites sprites;

	public FluidRendererImpl(ColorProviderRegistry colorProviderRegistry, LightPipelineProvider lighters) {
		this.colorProviderRegistry = colorProviderRegistry;
		defaultRenderer = new DefaultFluidRenderer(lighters);
		sprites = FluidSprites.create();
	}

	public void render(LevelSlice level, IBlockState blockState, IBlockState fluidState, BlockPos blockPos,
			BlockPos offset, TranslucentGeometryCollector collector, ChunkBuildBuffers buffers) {
		var material = DefaultMaterials.forFluidState(fluidState);
		var meshBuilder = buffers.get(material);
		var fluid = WorldUtil.getFluid(fluidState);

		defaultRenderer.render(level, blockState, blockPos, offset, collector, meshBuilder, material,
				colorProviderRegistry.getColorProvider(fluid), sprites.forFluid(fluid));
	}
}