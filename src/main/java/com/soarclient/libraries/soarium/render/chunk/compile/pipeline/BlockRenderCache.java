package com.soarclient.libraries.soarium.render.chunk.compile.pipeline;

import com.soarclient.libraries.soarium.model.color.ColorProviderRegistry;
import com.soarclient.libraries.soarium.model.light.LightPipelineProvider;
import com.soarclient.libraries.soarium.model.light.data.ArrayLightDataCache;
import com.soarclient.libraries.soarium.render.chunk.compile.pipeline.fluid.FluidRenderer;
import com.soarclient.libraries.soarium.render.chunk.compile.pipeline.fluid.FluidRendererImpl;
import com.soarclient.libraries.soarium.world.LevelSlice;
import com.soarclient.libraries.soarium.world.cloned.ChunkRenderContext;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BlockModelShapes;

public class BlockRenderCache {
	private final ArrayLightDataCache lightDataCache;

	private final BlockRenderer blockRenderer;
	private final FluidRenderer fluidRenderer;

	private final BlockModelShapes blockModels;
	private final LevelSlice levelSlice;

	public BlockRenderCache(Minecraft minecraft, WorldClient level) {
		this.levelSlice = new LevelSlice(level);
		this.lightDataCache = new ArrayLightDataCache(this.levelSlice);

		LightPipelineProvider lightPipelineProvider = new LightPipelineProvider(this.lightDataCache);

		var colorRegistry = new ColorProviderRegistry();

		this.blockRenderer = new BlockRenderer(colorRegistry, lightPipelineProvider);
		this.fluidRenderer = new FluidRendererImpl(colorRegistry, lightPipelineProvider);

		this.blockModels = minecraft.getBlockRendererDispatcher().getBlockModelShapes();
	}

	public BlockModelShapes getBlockModels() {
		return this.blockModels;
	}

	public BlockRenderer getBlockRenderer() {
		return this.blockRenderer;
	}

	public FluidRenderer getFluidRenderer() {
		return this.fluidRenderer;
	}

	public void init(ChunkRenderContext context) {
		this.lightDataCache.reset(context.origin());
		this.levelSlice.copyData(context);
	}

	public LevelSlice getWorldSlice() {
		return this.levelSlice;
	}

	public void cleanup() {
		this.levelSlice.reset();
	}
}
