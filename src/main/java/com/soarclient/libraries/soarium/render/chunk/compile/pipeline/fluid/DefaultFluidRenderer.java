package com.soarclient.libraries.soarium.render.chunk.compile.pipeline.fluid;

import java.util.Arrays;

import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

import com.soarclient.libraries.soarium.api.util.ColorARGB;
import com.soarclient.libraries.soarium.api.util.NormI8;
import com.soarclient.libraries.soarium.compat.Mth;
import com.soarclient.libraries.soarium.compat.minecraft.WorldUtil;
import com.soarclient.libraries.soarium.model.color.ColorProvider;
import com.soarclient.libraries.soarium.model.light.LightMode;
import com.soarclient.libraries.soarium.model.light.LightPipeline;
import com.soarclient.libraries.soarium.model.light.LightPipelineProvider;
import com.soarclient.libraries.soarium.model.light.data.QuadLightData;
import com.soarclient.libraries.soarium.model.quad.ModelQuad;
import com.soarclient.libraries.soarium.model.quad.ModelQuadView;
import com.soarclient.libraries.soarium.model.quad.ModelQuadViewMutable;
import com.soarclient.libraries.soarium.model.quad.properties.ModelQuadFacing;
import com.soarclient.libraries.soarium.model.quad.properties.ModelQuadFlags;
import com.soarclient.libraries.soarium.render.chunk.compile.buffers.ChunkModelBuilder;
import com.soarclient.libraries.soarium.render.chunk.compile.pipeline.BlockOcclusionCache;
import com.soarclient.libraries.soarium.render.chunk.terrain.material.Material;
import com.soarclient.libraries.soarium.render.chunk.translucent_sorting.TranslucentGeometryCollector;
import com.soarclient.libraries.soarium.render.chunk.vertex.format.ChunkVertexEncoder;
import com.soarclient.libraries.soarium.util.DirectionUtil;
import com.soarclient.libraries.soarium.world.LevelSlice;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;

public class DefaultFluidRenderer {
	// TODO: allow this to be changed by vertex format, WARNING: make sure
	// TranslucentGeometryCollector knows about EPSILON
	// TODO: move fluid rendering to a separate render pass and control
	// glPolygonOffset and glDepthFunc to fix this properly
	public static final float EPSILON = 0.001f;
	private static final float ALIGNED_EQUALS_EPSILON = 0.011f;

	private final BlockPos.MutableBlockPos scratchPos = new BlockPos.MutableBlockPos();
	private final MutableFloat scratchHeight = new MutableFloat(0);
	private final MutableInt scratchSamples = new MutableInt();

	private final BlockOcclusionCache occlusionCache = new BlockOcclusionCache();

	private final ModelQuadViewMutable quad = new ModelQuad();

	private final LightPipelineProvider lighters;

	private final QuadLightData quadLightData = new QuadLightData();
	private final int[] quadColors = new int[4];
	private final float[] brightness = new float[4];

	private final ChunkVertexEncoder.Vertex[] vertices = ChunkVertexEncoder.Vertex.uninitializedQuad();

	public DefaultFluidRenderer(LightPipelineProvider lighters) {
		this.quad.setLightFace(EnumFacing.UP);

		this.lighters = lighters;
	}

	private boolean isFluidOccluded(IBlockAccess world, BlockPos blockPos, EnumFacing dir, BlockLiquid fluid) {
		int x = blockPos.getX();
		int y = blockPos.getY();
		int z = blockPos.getZ();

		BlockPos pos = scratchPos.set(x, y, z);
		IBlockState blockState = world.getBlockState(pos);
		BlockPos adjPos = scratchPos.offset(dir);
		BlockLiquid adjFluid = WorldUtil.getFluid(world.getBlockState(adjPos));
		boolean temp = fluid == adjFluid;

		if (blockState.getBlock().getMaterial().isOpaque()) {
			return temp || blockState.getBlock().shouldSideBeRendered(world, pos, dir);
			// fluidlogged or next to water, occlude sides that are solid or the same liquid
		}
		return temp;
	}

	private boolean isSideExposed(IBlockAccess world, int x, int y, int z, EnumFacing dir) {
		BlockPos pos = scratchPos.set(x + dir.getFrontOffsetX(), y + dir.getFrontOffsetY(), z + dir.getFrontOffsetZ());
		IBlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();

		if (block.getMaterial().isOpaque()) {
			final boolean renderAsFullCube = block.isFullCube();

			// Hoist these checks to avoid allocating the shape below
			if (renderAsFullCube) {
				// The top face always be inset, so if the shape above is a full cube it can't
				// possibly occlude
				return dir == EnumFacing.UP;
			} else {
				return true;
			}
		}

		return true;
	}

	public void render(LevelSlice level, IBlockState fluidState, BlockPos blockPos, BlockPos offset,
			TranslucentGeometryCollector collector, ChunkModelBuilder meshBuilder, Material material,
			ColorProvider colorProvider, TextureAtlasSprite[] sprites) {
		int posX = blockPos.getX();
		int posY = blockPos.getY();
		int posZ = blockPos.getZ();

		BlockLiquid fluid = (BlockLiquid) fluidState.getBlock();

		boolean cullUp = this.isFluidOccluded(level, blockPos, EnumFacing.UP, fluid);
		boolean cullDown = this.isFluidOccluded(level, blockPos, EnumFacing.DOWN, fluid)
				|| !this.isSideExposed(level, posX, posY, posZ, EnumFacing.DOWN);
		boolean cullNorth = this.isFluidOccluded(level, blockPos, EnumFacing.NORTH, fluid);
		boolean cullSouth = this.isFluidOccluded(level, blockPos, EnumFacing.SOUTH, fluid);
		boolean cullWest = this.isFluidOccluded(level, blockPos, EnumFacing.WEST, fluid);
		boolean cullEast = this.isFluidOccluded(level, blockPos, EnumFacing.EAST, fluid);

		// stop rendering if all faces of the fluid are occluded
		if (cullUp && cullDown && cullEast && cullWest && cullNorth && cullSouth) {
			return;
		}

		boolean isWater = fluid.getMaterial() == net.minecraft.block.material.Material.water;

		float fluidHeight = this.fluidHeight(level, fluid, blockPos, EnumFacing.UP);
		float northWestHeight, southWestHeight, southEastHeight, northEastHeight;
		if (fluidHeight >= 1.0f) {
			northWestHeight = 1.0f;
			southWestHeight = 1.0f;
			southEastHeight = 1.0f;
			northEastHeight = 1.0f;
		} else {
			float heightNorth = this.fluidHeight(level, fluid, blockPos.offset(EnumFacing.NORTH), EnumFacing.NORTH);
			float heightSouth = this.fluidHeight(level, fluid, blockPos.offset(EnumFacing.SOUTH), EnumFacing.SOUTH);
			float heightEast = this.fluidHeight(level, fluid, blockPos.offset(EnumFacing.EAST), EnumFacing.EAST);
			float heightWest = this.fluidHeight(level, fluid, blockPos.offset(EnumFacing.WEST), EnumFacing.WEST);
			northWestHeight = this.fluidCornerHeight(level, fluid, fluidHeight, heightNorth, heightWest,
					blockPos.offset(EnumFacing.NORTH).offset(EnumFacing.WEST));
			southWestHeight = this.fluidCornerHeight(level, fluid, fluidHeight, heightSouth, heightWest,
					blockPos.offset(EnumFacing.SOUTH).offset(EnumFacing.WEST));
			southEastHeight = this.fluidCornerHeight(level, fluid, fluidHeight, heightSouth, heightEast,
					blockPos.offset(EnumFacing.SOUTH).offset(EnumFacing.EAST));
			northEastHeight = this.fluidCornerHeight(level, fluid, fluidHeight, heightNorth, heightEast,
					blockPos.offset(EnumFacing.NORTH).offset(EnumFacing.EAST));
		}
		float yOffset = cullDown ? 0.0F : EPSILON;

		final ModelQuadViewMutable quad = this.quad;

		LightMode lightMode = isWater && Minecraft.isAmbientOcclusionEnabled() ? LightMode.SMOOTH : LightMode.FLAT;
		LightPipeline lighter = this.lighters.getLighter(lightMode);

		quad.setFlags(0);

		var flow = BlockLiquid.getFlowingBlock(fluid.getMaterial());

		if (!cullUp && this.isSideExposed(level, posX, posY, posZ, EnumFacing.UP)) {
			northWestHeight -= EPSILON;
			southWestHeight -= EPSILON;
			southEastHeight -= EPSILON;
			northEastHeight -= EPSILON;

			Vec3 velocity = flow.getFlowVector(level, blockPos);

			TextureAtlasSprite sprite;
			float u1, u2, u3, u4;
			float v1, v2, v3, v4;

			if (velocity.xCoord == 0.0D && velocity.zCoord == 0.0D) {
				sprite = sprites[0];
				u1 = sprite.getInterpolatedU(0.0D);
				v1 = sprite.getInterpolatedV(0.0D);
				u2 = u1;
				v2 = sprite.getInterpolatedV(16.0D);
				u3 = sprite.getInterpolatedU(16.0D);
				v3 = v2;
				u4 = u3;
				v4 = v1;
			} else {
				sprite = sprites[1];
				float dir = (float) MathHelper.atan2(velocity.zCoord, velocity.xCoord) - (1.5707964f);
				float sin = MathHelper.sin(dir) * 0.25F;
				float cos = MathHelper.cos(dir) * 0.25F;
				u1 = sprite.getInterpolatedU(8.0F + (-cos - sin) * 16.0F);
				v1 = sprite.getInterpolatedV(8.0F + (-cos + sin) * 16.0F);
				u2 = sprite.getInterpolatedU(8.0F + (-cos + sin) * 16.0F);
				v2 = sprite.getInterpolatedV(8.0F + (cos + sin) * 16.0F);
				u3 = sprite.getInterpolatedU(8.0F + (cos + sin) * 16.0F);
				v3 = sprite.getInterpolatedV(8.0F + (cos - sin) * 16.0F);
				u4 = sprite.getInterpolatedU(8.0F + (cos - sin) * 16.0F);
				v4 = sprite.getInterpolatedV(8.0F + (-cos - sin) * 16.0F);
			}

			float uAvg = (u1 + u2 + u3 + u4) / 4.0F;
			float vAvg = (v1 + v2 + v3 + v4) / 4.0F;

			float s1 = (float) sprites[0].getIconWidth() / (sprites[0].getMaxU() - sprites[0].getMinU());
			float s2 = (float) sprites[0].getIconHeight() / (sprites[0].getMaxV() - sprites[0].getMinV());
			float s3 = 4.0F / Math.max(s2, s1);

			u1 = Mth.lerp(s3, u1, uAvg);
			u2 = Mth.lerp(s3, u2, uAvg);
			u3 = Mth.lerp(s3, u3, uAvg);
			u4 = Mth.lerp(s3, u4, uAvg);
			v1 = Mth.lerp(s3, v1, vAvg);
			v2 = Mth.lerp(s3, v2, vAvg);
			v3 = Mth.lerp(s3, v3, vAvg);
			v4 = Mth.lerp(s3, v4, vAvg);

			quad.setSprite(sprite);

			// top surface alignedness is calculated with a more relaxed epsilon
			boolean aligned = isAlignedEquals(northEastHeight, northWestHeight)
					&& isAlignedEquals(northWestHeight, southEastHeight)
					&& isAlignedEquals(southEastHeight, southWestHeight)
					&& isAlignedEquals(southWestHeight, northEastHeight);

			boolean creaseNorthEastSouthWest = aligned
					|| northEastHeight > northWestHeight && northEastHeight > southEastHeight
					|| northEastHeight < northWestHeight && northEastHeight < southEastHeight
					|| southWestHeight > northWestHeight && southWestHeight > southEastHeight
					|| southWestHeight < northWestHeight && southWestHeight < southEastHeight;

			if (creaseNorthEastSouthWest) {
				setVertex(quad, 1, 0.0f, northWestHeight, 0.0f, u1, v1);
				setVertex(quad, 2, 0.0f, southWestHeight, 1.0F, u2, v2);
				setVertex(quad, 3, 1.0F, southEastHeight, 1.0F, u3, v3);
				setVertex(quad, 0, 1.0F, northEastHeight, 0.0f, u4, v4);
			} else {
				setVertex(quad, 0, 0.0f, northWestHeight, 0.0f, u1, v1);
				setVertex(quad, 1, 0.0f, southWestHeight, 1.0F, u2, v2);
				setVertex(quad, 2, 1.0F, southEastHeight, 1.0F, u3, v3);
				setVertex(quad, 3, 1.0F, northEastHeight, 0.0f, u4, v4);
			}

			this.updateQuad(quad, level, blockPos, lighter, EnumFacing.UP, ModelQuadFacing.POS_Y, 1.0F, colorProvider,
					fluidState);
			this.writeQuad(meshBuilder, collector, material, offset, quad,
					aligned ? ModelQuadFacing.POS_Y : ModelQuadFacing.UNASSIGNED, false);

			if (WorldUtil.method_15756(level, this.scratchPos.set(posX, posY + 1, posZ), fluid)) {
				this.writeQuad(meshBuilder, collector, material, offset, quad,
						aligned ? ModelQuadFacing.NEG_Y : ModelQuadFacing.UNASSIGNED, true);
			}
		}

		if (!cullDown) {
			TextureAtlasSprite sprite = sprites[0];

			float minU = sprite.getMinU();
			float maxU = sprite.getMaxU();
			float minV = sprite.getMinV();
			float maxV = sprite.getMaxV();
			quad.setSprite(sprite);

			setVertex(quad, 0, 0.0f, yOffset, 1.0F, minU, maxV);
			setVertex(quad, 1, 0.0f, yOffset, 0.0f, minU, minV);
			setVertex(quad, 2, 1.0F, yOffset, 0.0f, maxU, minV);
			setVertex(quad, 3, 1.0F, yOffset, 1.0F, maxU, maxV);

			this.updateQuad(quad, level, blockPos, lighter, EnumFacing.DOWN, ModelQuadFacing.NEG_Y, 1.0F, colorProvider,
					fluidState);
			this.writeQuad(meshBuilder, collector, material, offset, quad, ModelQuadFacing.NEG_Y, false);
		}

		quad.setFlags(ModelQuadFlags.IS_PARALLEL | ModelQuadFlags.IS_ALIGNED);

		for (EnumFacing dir : DirectionUtil.HORIZONTAL_DIRECTIONS) {
			float c1;
			float c2;
			float x1;
			float z1;
			float x2;
			float z2;

			switch (dir) {
			case NORTH -> {
				if (cullNorth) {
					continue;
				}
				c1 = northWestHeight;
				c2 = northEastHeight;
				x1 = 0.0f;
				x2 = 1.0F;
				z1 = EPSILON;
				z2 = z1;
			}
			case SOUTH -> {
				if (cullSouth) {
					continue;
				}
				c1 = southEastHeight;
				c2 = southWestHeight;
				x1 = 1.0F;
				x2 = 0.0f;
				z1 = 1.0f - EPSILON;
				z2 = z1;
			}
			case WEST -> {
				if (cullWest) {
					continue;
				}
				c1 = southWestHeight;
				c2 = northWestHeight;
				x1 = EPSILON;
				x2 = x1;
				z1 = 1.0F;
				z2 = 0.0f;
			}
			case EAST -> {
				if (cullEast) {
					continue;
				}
				c1 = northEastHeight;
				c2 = southEastHeight;
				x1 = 1.0f - EPSILON;
				x2 = x1;
				z1 = 0.0f;
				z2 = 1.0F;
			}
			default -> {
				continue;
			}
			}

			if (this.isSideExposed(level, posX, posY, posZ, dir)) {
				TextureAtlasSprite sprite = sprites[1];

				float u1 = sprite.getInterpolatedU(0.0F);
				float u2 = sprite.getInterpolatedU(8F);
				float v1 = sprite.getInterpolatedV((1.0F - c1) * 16.0F * 0.5F);
				float v2 = sprite.getInterpolatedV((1.0F - c2) * 16.0F * 0.5F);
				float v3 = sprite.getInterpolatedV(8.0F);

				quad.setSprite(sprite);

				setVertex(quad, 0, x2, c2, z2, u2, v2);
				setVertex(quad, 1, x2, yOffset, z2, u2, v3);
				setVertex(quad, 2, x1, yOffset, z1, u1, v3);
				setVertex(quad, 3, x1, c1, z1, u1, v1);

				float br = dir.getAxis() == EnumFacing.Axis.Z ? 0.8F : 0.6F;

				ModelQuadFacing facing = ModelQuadFacing.fromDirection(dir);

				this.updateQuad(quad, level, blockPos, lighter, dir, facing, br, colorProvider, fluidState);
				this.writeQuad(meshBuilder, collector, material, offset, quad, facing, false);
				this.writeQuad(meshBuilder, collector, material, offset, quad, facing.getOpposite(), true);
			}
		}
	}

	private static boolean isAlignedEquals(float a, float b) {
		return Math.abs(a - b) <= ALIGNED_EQUALS_EPSILON;
	}

	private void updateQuad(ModelQuadViewMutable quad, LevelSlice level, BlockPos pos, LightPipeline lighter,
			EnumFacing dir, ModelQuadFacing facing, float brightness, ColorProvider colorProvider,
			IBlockState fluidState) {

		int normal;
		if (facing.isAligned()) {
			normal = facing.getPackedAlignedNormal();
		} else {
			normal = quad.calculateNormal();
		}

		quad.setFaceNormal(normal);

		QuadLightData light = this.quadLightData;

		lighter.calculate(quad, pos, light, null, dir, false, false);

		if (colorProvider != null && quad.hasTintIndex()) {
			colorProvider.getColors(level, fluidState, quad, this.quadColors, pos);
		} else {
			Arrays.fill(this.quadColors, 0xFFFFFF);
		}

		// multiply the per-vertex color against the combined brightness
		// the combined brightness is the per-vertex brightness multiplied by the
		// block's brightness
		for (int i = 0; i < 4; i++) {
			this.quadColors[i] = ColorARGB.toABGR(this.quadColors[i]) | 0xFF000000;
			this.brightness[i] = light.br[i] * brightness;
		}
	}

	private void writeQuad(ChunkModelBuilder builder, TranslucentGeometryCollector collector, Material material,
			BlockPos offset, ModelQuadView quad, ModelQuadFacing facing, boolean flip) {
		var vertices = this.vertices;

		for (int i = 0; i < 4; i++) {
			var out = vertices[flip ? (3 - i + 1) & 0b11 : i];
			out.x = offset.getX() + quad.getX(i);
			out.y = offset.getY() + quad.getY(i);
			out.z = offset.getZ() + quad.getZ(i);

			out.color = this.quadColors[i];
			out.ao = this.brightness[i];
			out.u = quad.getTexU(i);
			out.v = quad.getTexV(i);
			out.light = this.quadLightData.lm[i];
		}

		TextureAtlasSprite sprite = quad.getSprite();

		if (sprite != null) {
			builder.addSprite(sprite);
		}

		if (material.isTranslucent() && collector != null) {
			int normal;

			if (facing.isAligned()) {
				normal = facing.getPackedAlignedNormal();
			} else {
				// This was updated earlier in updateQuad. There is no situation where the
				// normal vector should have changed.
				normal = quad.getFaceNormal();
			}

			if (flip) {
				normal = NormI8.flipPacked(normal);
			}

			collector.appendQuad(normal, vertices, facing);
		}

		var vertexBuffer = builder.getVertexBuffer(facing);
		vertexBuffer.push(vertices, material);
	}

	private static void setVertex(ModelQuadViewMutable quad, int i, float x, float y, float z, float u, float v) {
		quad.setX(i, x);
		quad.setY(i, y);
		quad.setZ(i, z);
		quad.setTexU(i, u);
		quad.setTexV(i, v);
	}

	private float fluidCornerHeight(IBlockAccess world, BlockLiquid fluid, float fluidHeight, float fluidHeightX,
			float fluidHeightY, BlockPos blockPos) {
		if (fluidHeightY >= 1.0f || fluidHeightX >= 1.0f) {
			return 1.0f;
		}

		if (fluidHeightY > 0.0f || fluidHeightX > 0.0f) {
			float height = this.fluidHeight(world, fluid, blockPos, EnumFacing.UP);

			if (height >= 1.0f) {
				return 1.0f;
			}

			this.modifyHeight(this.scratchHeight, this.scratchSamples, height);
		}

		this.modifyHeight(this.scratchHeight, this.scratchSamples, fluidHeight);
		this.modifyHeight(this.scratchHeight, this.scratchSamples, fluidHeightY);
		this.modifyHeight(this.scratchHeight, this.scratchSamples, fluidHeightX);

		float result = this.scratchHeight.floatValue() / this.scratchSamples.intValue();
		this.scratchHeight.setValue(0);
		this.scratchSamples.setValue(0);

		return result;
	}

	private void modifyHeight(MutableFloat totalHeight, MutableInt samples, float target) {
		if (target >= 0.8f) {
			totalHeight.add(target * 10.0f);
			samples.add(10);
		} else if (target >= 0.0f) {
			totalHeight.add(target);
			samples.increment();
		}
	}

	private float fluidHeight(IBlockAccess world, BlockLiquid fluid, BlockPos blockPos, EnumFacing direction) {
		IBlockState blockState = world.getBlockState(blockPos);

		if (blockState.getBlock().getMaterial() == fluid.getMaterial()) {
			IBlockState fluidStateUp = world.getBlockState(blockPos.up());

			if (fluidStateUp.getBlock().getMaterial() == fluid.getMaterial()) {
				return 1.0f;
			} else {
				try {
					return 1f - BlockLiquid.getLiquidHeightPercent(blockState.getValue(BlockLiquid.LEVEL));
				} catch (Exception e) {
					return 0f;
				}
			}
		}
		if (!blockState.getBlock().isFullBlock()) {
			return 0.0f;
		}
		return -1.0f;
	}
}