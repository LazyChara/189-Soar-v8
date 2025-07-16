package com.soarclient.libraries.soarium.render.chunk.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.IntFunction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.soarclient.libraries.soarium.render.chunk.RenderSectionFlags;
import com.soarclient.libraries.soarium.render.chunk.occlusion.VisibilityEncoding;
import com.soarclient.libraries.soarium.render.chunk.terrain.TerrainRenderPass;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.renderer.chunk.SetVisibility;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 * The render data for a chunk render container containing all the information
 * about which meshes are attached, the block entities contained by it, and any
 * data used for occlusion testing.
 */
public class BuiltSectionInfo {
	public static final BuiltSectionInfo EMPTY = createEmptyData();

	public final int flags;
	public final long visibilityData;

	public final TileEntity @Nullable [] globalBlockEntities;
	public final TileEntity @Nullable [] culledBlockEntities;
	public final TextureAtlasSprite @Nullable [] animatedSprites;

	private BuiltSectionInfo(@NotNull Collection<TerrainRenderPass> blockRenderPasses,
			@NotNull Collection<TileEntity> globalBlockEntities, @NotNull Collection<TileEntity> culledBlockEntities,
			@NotNull Collection<TextureAtlasSprite> animatedSprites, @NotNull SetVisibility occlusionData) {
		this.globalBlockEntities = toArray(globalBlockEntities, TileEntity[]::new);
		this.culledBlockEntities = toArray(culledBlockEntities, TileEntity[]::new);
		this.animatedSprites = toArray(animatedSprites, TextureAtlasSprite[]::new);

		int flags = 0;

		if (!blockRenderPasses.isEmpty()) {
			flags |= 1 << RenderSectionFlags.HAS_BLOCK_GEOMETRY;
		}

		if (!culledBlockEntities.isEmpty()) {
			flags |= 1 << RenderSectionFlags.HAS_BLOCK_ENTITIES;
		}

		if (!animatedSprites.isEmpty()) {
			flags |= 1 << RenderSectionFlags.HAS_ANIMATED_SPRITES;
		}

		this.flags = flags;

		this.visibilityData = VisibilityEncoding.encode(occlusionData);
	}

	public static class Builder {
		private final List<TerrainRenderPass> blockRenderPasses = new ArrayList<>();
		private final List<TileEntity> globalBlockEntities = new ArrayList<>();
		private final List<TileEntity> culledBlockEntities = new ArrayList<>();
		private final Set<TextureAtlasSprite> animatedSprites = new ObjectOpenHashSet<>();

		private SetVisibility occlusionData;

		public void addRenderPass(TerrainRenderPass pass) {
			this.blockRenderPasses.add(pass);
		}

		public void setOcclusionData(SetVisibility data) {
			this.occlusionData = data;
		}

		/**
		 * Adds a sprite to this data container for tracking. If the sprite is tickable,
		 * it will be ticked every frame before rendering as necessary.
		 * 
		 * @param sprite The sprite
		 */
		public void addSprite(TextureAtlasSprite sprite) {
			if (sprite.hasAnimationMetadata()) {
				this.animatedSprites.add(sprite);
			}
		}

		/**
		 * Adds a block entity to the data container.
		 * 
		 * @param entity The block entity itself
		 * @param cull   True if the block entity can be culled to this chunk render's
		 *               volume, otherwise false
		 */
		public void addBlockEntity(TileEntity entity, boolean cull) {
			(cull ? this.culledBlockEntities : this.globalBlockEntities).add(entity);
		}

		public BuiltSectionInfo build() {
			return new BuiltSectionInfo(this.blockRenderPasses, this.globalBlockEntities, this.culledBlockEntities,
					this.animatedSprites, this.occlusionData);
		}
	}

	private static BuiltSectionInfo createEmptyData() {
		SetVisibility occlusionData = new SetVisibility();
		occlusionData.setManyVisible(EnumSet.allOf(EnumFacing.class));

		BuiltSectionInfo.Builder meshInfo = new BuiltSectionInfo.Builder();
		meshInfo.setOcclusionData(occlusionData);

		return meshInfo.build();
	}

	private static <T> T[] toArray(Collection<T> collection, IntFunction<T[]> allocator) {
		if (collection.isEmpty()) {
			return null;
		}

		return collection.toArray(allocator);
	}
}