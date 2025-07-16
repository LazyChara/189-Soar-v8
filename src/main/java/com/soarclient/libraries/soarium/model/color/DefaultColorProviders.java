package com.soarclient.libraries.soarium.model.color;

import com.soarclient.libraries.soarium.model.quad.ModelQuadView;
import com.soarclient.libraries.soarium.model.quad.blender.BlendedColorProvider;
import com.soarclient.libraries.soarium.world.LevelSlice;
import com.soarclient.libraries.soarium.world.biome.BiomeColorSource;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

public class DefaultColorProviders {
	public static final BlockColorProvider BLOCK = new BlockColorProvider();
	public static final WaterColorProvider WATER = new WaterColorProvider();
	public static final GrassColorProvider GRASS = new GrassColorProvider();
	public static final FoliageColorProvider FOLIAGE = new FoliageColorProvider();

	public static class BlockColorProvider implements ColorProvider {
		@Override
		public void getColors(LevelSlice slice, IBlockState state, ModelQuadView quad, int[] output, BlockPos pos) {
			var block = slice.getBlockState(pos).getBlock();
			for (int i = 0; i < output.length; i++) {
				output[i] = block.colorMultiplier(slice, pos, i);
			}
		}
	}

	public static class WaterColorProvider extends BlendedColorProvider {
		@Override
		protected int getColor(LevelSlice slice, BlockPos pos) {
			return slice.getColor(BiomeColorSource.WATER, pos.getX(), pos.getY(), pos.getZ());
		}
	}

	public static class GrassColorProvider extends BlendedColorProvider {
		@Override
		protected int getColor(LevelSlice slice, BlockPos pos) {
			return slice.getColor(BiomeColorSource.GRASS, pos.getX(), pos.getY(), pos.getZ());
		}
	}

	public static class FoliageColorProvider extends BlendedColorProvider {
		@Override
		protected int getColor(LevelSlice slice, BlockPos pos) {
			return slice.getColor(BiomeColorSource.FOLIAGE, pos.getX(), pos.getY(), pos.getZ());
		}
	}
}
