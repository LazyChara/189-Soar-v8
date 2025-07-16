package com.soarclient.libraries.soarium.model.color;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;

public class ColorProviderRegistry {
	private final Reference2ReferenceMap<Block, ColorProvider> blocks = new Reference2ReferenceOpenHashMap<>();
	private final Reference2ReferenceMap<Block, ColorProvider> fluids = new Reference2ReferenceOpenHashMap<>();

	public ColorProviderRegistry() {
		this.installOverrides();
		this.blocks.defaultReturnValue(DefaultColorProviders.BLOCK);
	}

	// TODO: Allow mods to install their own color resolvers here
	private void installOverrides() {
		this.registerBlocks(DefaultColorProviders.GRASS, Blocks.grass, Blocks.reeds, Blocks.tallgrass,
				Blocks.double_plant);
		this.registerBlocks(DefaultColorProviders.FOLIAGE, Blocks.leaves, Blocks.leaves2, Blocks.vine);
		this.registerBlocks(DefaultColorProviders.WATER, Blocks.water);
		this.registerFluids(DefaultColorProviders.WATER, Blocks.water, Blocks.flowing_water);
	}

	private void registerBlocks(ColorProvider provider, Block... blocks) {
		for (var block : blocks) {
			this.blocks.put(block, provider);
		}
	}

	private void registerFluids(ColorProvider provider, BlockLiquid... fluids) {
		for (var fluid : fluids) {
			this.fluids.put(fluid, provider);
		}
	}

	public ColorProvider getColorProvider(Block block) {
		return this.blocks.get(block);
	}

	public ColorProvider getColorProvider(BlockLiquid fluid) {
		return this.fluids.get(fluid);
	}
}
