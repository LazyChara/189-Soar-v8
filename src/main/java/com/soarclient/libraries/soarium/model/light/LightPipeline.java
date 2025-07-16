package com.soarclient.libraries.soarium.model.light;

import com.soarclient.libraries.soarium.model.light.data.QuadLightData;
import com.soarclient.libraries.soarium.model.quad.ModelQuadView;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * Light pipelines allow model quads for any location in the level to be lit
 * using various backends, including fluids and block entities.
 */
public interface LightPipeline {
	/**
	 * Calculates the light data for a given block model quad, storing the result in
	 * {@param out}.
	 * 
	 * @param quad      The block model quad
	 * @param pos       The block position of the model this quad belongs to
	 * @param out       The data arrays which will store the calculated light data
	 *                  results
	 * @param cullFace  The cull face of the quad
	 * @param lightFace The light face of the quad
	 * @param shade     True if the block is shaded by ambient occlusion
	 * @param enhanced  Whether the quad should use normal-based irregular lighting
	 */
	void calculate(ModelQuadView quad, BlockPos pos, QuadLightData out, EnumFacing cullFace, EnumFacing lightFace,
			boolean shade, boolean enhanced);
}
