package com.soarclient.libraries.soarium.render.chunk.compile.pipeline;

import java.util.Arrays;
import java.util.List;

import com.soarclient.libraries.soarium.api.util.ColorARGB;
import com.soarclient.libraries.soarium.model.color.ColorProvider;
import com.soarclient.libraries.soarium.model.color.ColorProviderRegistry;
import com.soarclient.libraries.soarium.model.light.LightMode;
import com.soarclient.libraries.soarium.model.light.LightPipeline;
import com.soarclient.libraries.soarium.model.light.LightPipelineProvider;
import com.soarclient.libraries.soarium.model.light.data.QuadLightData;
import com.soarclient.libraries.soarium.model.quad.BakedQuadView;
import com.soarclient.libraries.soarium.model.quad.properties.ModelQuadFacing;
import com.soarclient.libraries.soarium.model.quad.properties.ModelQuadOrientation;
import com.soarclient.libraries.soarium.render.chunk.compile.ChunkBuildBuffers;
import com.soarclient.libraries.soarium.render.chunk.compile.buffers.ChunkModelBuilder;
import com.soarclient.libraries.soarium.render.chunk.terrain.material.DefaultMaterials;
import com.soarclient.libraries.soarium.render.chunk.terrain.material.Material;
import com.soarclient.libraries.soarium.render.chunk.vertex.format.ChunkVertexEncoder;
import com.soarclient.libraries.soarium.util.DirectionUtil;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class BlockRenderer {
	private final ColorProviderRegistry colorProviderRegistry;
	private final BlockOcclusionCache occlusionCache;

	private final QuadLightData quadLightData = new QuadLightData();

	private final LightPipelineProvider lighters;

	private final ChunkVertexEncoder.Vertex[] vertices = ChunkVertexEncoder.Vertex.uninitializedQuad();

	private final boolean useAmbientOcclusion;

	private final int[] quadColors = new int[4];

	public BlockRenderer(ColorProviderRegistry colorRegistry, LightPipelineProvider lighters) {
		this.colorProviderRegistry = colorRegistry;
		this.lighters = lighters;

		this.occlusionCache = new BlockOcclusionCache();
		this.useAmbientOcclusion = Minecraft.isAmbientOcclusionEnabled();
	}

	public void renderModel(BlockRenderContext ctx, ChunkBuildBuffers buffers) {
		var material = DefaultMaterials.forBlockState(ctx.state());
		var meshBuilder = buffers.get(material);

		ColorProvider colorizer = this.colorProviderRegistry.getColorProvider(ctx.state().getBlock());

		LightPipeline lighter = this.lighters.getLighter(this.getLightingMode(ctx.state(), ctx.model()));
		Vec3 renderOffset = new Vec3(0, 0, 0);

		var offsetType = ctx.state().getBlock().getOffsetType();

		if (offsetType != Block.EnumOffsetType.NONE) {
			int x = (int) ctx.origin().x();
			int z = (int) ctx.origin().z();
			// Taken from MathHelper.hashCode()
			long i = (x * 3129871L) ^ z * 116129781L;
			i = i * i * 42317861L + i * 11L;

			double fx = (((i >> 16 & 15L) / 15.0F) - 0.5f) * 0.5f;
			double fz = (((i >> 24 & 15L) / 15.0F) - 0.5f) * 0.5f;
			double fy = 0;

			if (offsetType == Block.EnumOffsetType.XYZ) {
				fy += (((i >> 20 & 15L) / 15.0F) - 1.0f) * 0.2f;
			}

			renderOffset.addVector((float) fx, (float) fy, (float) fz);
		}

		for (EnumFacing face : DirectionUtil.ALL_DIRECTIONS) {
			List<BakedQuad> quads = this.getGeometry(ctx, face);

			if (!quads.isEmpty() && this.isFaceVisible(ctx, face)) {
				this.renderQuadList(ctx, material, lighter, colorizer, renderOffset, meshBuilder, quads, face);
			}
		}

		List<BakedQuad> all = this.getGeometry(ctx, null);

		if (!all.isEmpty()) {
			this.renderQuadList(ctx, material, lighter, colorizer, renderOffset, meshBuilder, all, null);
		}
	}

	private List<BakedQuad> getGeometry(BlockRenderContext ctx, EnumFacing face) {
		var model = ctx.model();
		return face == null ? model.getGeneralQuads() : model.getFaceQuads(face);
	}

	private boolean isFaceVisible(BlockRenderContext ctx, EnumFacing face) {
		return this.occlusionCache.shouldDrawSide(ctx.slice(), ctx.pos(), face);
	}

	private void renderQuadList(BlockRenderContext ctx, Material material, LightPipeline lighter,
			ColorProvider colorizer, Vec3 offset, ChunkModelBuilder builder, List<BakedQuad> quads,
			EnumFacing cullFace) {

		// This is a very hot allocation, iterate over it manually
		// noinspection ForLoopReplaceableByForEach
		for (int i = 0, quadsSize = quads.size(); i < quadsSize; i++) {
			BakedQuadView quad = (BakedQuadView) quads.get(i);

			final var lightData = this.getVertexLight(ctx, lighter, cullFace, quad);
			final var vertexColors = this.getVertexColors(ctx, colorizer, quad);

			this.writeGeometry(ctx, builder, offset, material, quad, vertexColors, lightData);

			TextureAtlasSprite sprite = quad.getSprite();

			if (sprite != null) {
				builder.addSprite(sprite);
			}
		}
	}

	private QuadLightData getVertexLight(BlockRenderContext ctx, LightPipeline lighter, EnumFacing cullFace,
			BakedQuadView quad) {
		QuadLightData light = this.quadLightData;
		lighter.calculate(quad, ctx.pos(), light, cullFace, quad.getLightFace(), true, false);

		return light;
	}

	private int[] getVertexColors(BlockRenderContext ctx, ColorProvider colorProvider, BakedQuadView quad) {
		final int[] vertexColors = this.quadColors;

		if (colorProvider != null && quad.hasTintIndex()) {
			colorProvider.getColors(ctx.slice(), ctx.state(), quad, vertexColors, ctx.pos());
		} else {
			Arrays.fill(vertexColors, 0xFFFFFF);
		}

		return vertexColors;
	}

	private void writeGeometry(BlockRenderContext ctx, ChunkModelBuilder builder, Vec3 offset, Material material,
			BakedQuadView quad, int[] colors, QuadLightData light) {
		ModelQuadOrientation orientation = ModelQuadOrientation.orientByBrightness(light.br, light.lm);
		var vertices = this.vertices;

		ModelQuadFacing normalFace = quad.getNormalFace();

		for (int dstIndex = 0; dstIndex < 4; dstIndex++) {
			int srcIndex = orientation.getVertexIndex(dstIndex);

			var out = vertices[dstIndex];
			out.x = ctx.origin().x() + quad.getX(srcIndex) + (float) offset.xCoord;
			out.y = ctx.origin().y() + quad.getY(srcIndex) + (float) offset.yCoord;
			out.z = ctx.origin().z() + quad.getZ(srcIndex) + (float) offset.zCoord;

			out.color = ColorARGB.toABGR(colors[srcIndex]) | 0xFF000000;
			out.ao = light.br[srcIndex];

			out.u = quad.getTexU(srcIndex);
			out.v = quad.getTexV(srcIndex);

			out.light = light.lm[srcIndex];
		}

		if (material.isTranslucent() && ctx.collector != null) {
			ctx.collector.appendQuad(quad.getFaceNormal(), vertices, normalFace);
		}

		var vertexBuffer = builder.getVertexBuffer(normalFace);
		vertexBuffer.push(vertices, material);
	}

	private LightMode getLightingMode(IBlockState state, IBakedModel model) {
		if (this.useAmbientOcclusion && model.isAmbientOcclusion() && state.getBlock().getLightValue() == 0) {
			return LightMode.SMOOTH;
		} else {
			return LightMode.FLAT;
		}
	}
}