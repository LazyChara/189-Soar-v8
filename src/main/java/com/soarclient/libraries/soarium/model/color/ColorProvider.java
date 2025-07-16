package com.soarclient.libraries.soarium.model.color;

import com.soarclient.libraries.soarium.model.quad.ModelQuadView;
import com.soarclient.libraries.soarium.world.LevelSlice;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

public interface ColorProvider {
	/**
	 * Computes the per-vertex colors of a model quad and stores the results in
	 * {@param output}. The order of the output color array is the same as the order
	 * of the quad's vertices.
	 *
	 * @param slice  The level slice which contains the object being colorized
	 * @param state
	 * @param quad   The quad geometry which should be colorized
	 * @param output The output array of vertex colors (in ABGR format)
	 * @param pos    The position of the object being colorized
	 */
	void getColors(LevelSlice slice, IBlockState state, ModelQuadView quad, int[] output, BlockPos pos);
}
