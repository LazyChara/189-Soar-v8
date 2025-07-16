package com.soarclient.libraries.soarium.util;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockFluidRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

/**
 * Caches fluid sprites and quickly allows you to access them for maximum
 * efficiency.
 *
 * @author Lunasa
 */
public class FluidSprites {
	private final TextureAtlasSprite[] waterSprites;
	private final TextureAtlasSprite[] lavaSprites;

	public FluidSprites(TextureAtlasSprite[] waterSprites, TextureAtlasSprite[] lavaSprites) {
		this.waterSprites = waterSprites;
		this.lavaSprites = lavaSprites;
	}

	public TextureAtlasSprite[] forFluid(BlockLiquid fluidBlock) {
		if (fluidBlock.getMaterial() == Material.water)
			return this.waterSprites;
		return this.lavaSprites;
	}

	public static FluidSprites create() {
		return new FluidSprites(getFluidRenderer().getAtlasSpritesWater(), getFluidRenderer().getAtlasSpritesLava());
	}

	private static BlockFluidRenderer getFluidRenderer() {
		return Minecraft.getMinecraft().getBlockRendererDispatcher().getFluidRenderer();
	}
}
