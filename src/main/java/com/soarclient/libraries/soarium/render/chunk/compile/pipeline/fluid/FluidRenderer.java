package com.soarclient.libraries.soarium.render.chunk.compile.pipeline.fluid;

import com.soarclient.libraries.soarium.render.chunk.compile.ChunkBuildBuffers;
import com.soarclient.libraries.soarium.render.chunk.translucent_sorting.TranslucentGeometryCollector;
import com.soarclient.libraries.soarium.world.LevelSlice;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

public abstract class FluidRenderer {
	public abstract void render(LevelSlice level, IBlockState blockState, IBlockState fluidState, BlockPos blockPos,
			BlockPos offset, TranslucentGeometryCollector collector, ChunkBuildBuffers buffers);
}
