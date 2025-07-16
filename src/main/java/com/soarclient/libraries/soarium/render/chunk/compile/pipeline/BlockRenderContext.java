package com.soarclient.libraries.soarium.render.chunk.compile.pipeline;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.BlockPos;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.soarclient.libraries.soarium.render.chunk.translucent_sorting.TranslucentGeometryCollector;
import com.soarclient.libraries.soarium.world.LevelSlice;

public class BlockRenderContext {
	private final LevelSlice slice;
	public final TranslucentGeometryCollector collector;

	private final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

	private final Vector3f origin = new Vector3f();

	private IBlockState state;
	private IBakedModel model;

	public BlockRenderContext(LevelSlice slice, TranslucentGeometryCollector collector) {
		this.slice = slice;
		this.collector = collector;
	}

	public void update(BlockPos pos, BlockPos origin, IBlockState state, IBakedModel model) {
		this.pos.set(pos.getX(), pos.getY(), pos.getZ());
		this.origin.set(origin.getX(), origin.getY(), origin.getZ());

		this.state = state;
		this.model = model;
	}

	/**
	 * @return The collector for translucent geometry sorting
	 */
	public TranslucentGeometryCollector collector() {
		return this.collector;
	}

	/**
	 * @return The position (in block space) of the block being rendered
	 */
	public BlockPos pos() {
		return this.pos;
	}

	/**
	 * @return The level which the block is being rendered from
	 */
	public LevelSlice slice() {
		return this.slice;
	}

	/**
	 * @return The state of the block being rendered
	 */
	public IBlockState state() {
		return this.state;
	}

	/**
	 * @return The model used for this block
	 */
	public IBakedModel model() {
		return this.model;
	}

	/**
	 * @return The origin of the block within the model
	 */
	public Vector3fc origin() {
		return this.origin;
	}
}
