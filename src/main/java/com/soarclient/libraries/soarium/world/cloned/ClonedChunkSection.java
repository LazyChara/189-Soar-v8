package com.soarclient.libraries.soarium.world.cloned;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.soarclient.libraries.soarium.compat.minecraft.math.SectionPos;
import com.soarclient.libraries.soarium.world.LevelSlice;

import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class ClonedChunkSection {
	private static final NibbleArray DEFAULT_SKY_LIGHT_ARRAY = new NibbleArray();
	private static final NibbleArray DEFAULT_BLOCK_LIGHT_ARRAY = new NibbleArray();

	private final SectionPos pos;

	private final @Nullable Int2ReferenceMap<TileEntity> blockEntityMap;

	private @Nullable NibbleArray[] lightDataArrays;

	private final @Nullable char[] blockData;
	private final @Nullable BiomeGenBase[] biomeData;

	private long lastUsedTimestamp = Long.MAX_VALUE;

	private final ExtendedBlockStorage section;
	private final World level;
	private final Chunk chunk;

	public ClonedChunkSection(World level, Chunk chunk, @Nullable ExtendedBlockStorage section, SectionPos pos) {
		this.pos = pos;
		this.chunk = chunk;

		char[] blockData = null;
		BiomeGenBase[] biomeData = null;

		Int2ReferenceMap<TileEntity> blockEntityMap = null;

		if (section != null) {
			if (!section.isEmpty()) {
				blockData = section.getData();

				blockEntityMap = copyBlockEntities(chunk, pos);
			}

			biomeData = convertBiomeArray(chunk.getBiomeArray());
		}

		this.blockData = blockData;
		this.biomeData = biomeData;

		this.blockEntityMap = blockEntityMap;

		this.lightDataArrays = copyLightData(level, section);

		this.section = section;

		this.level = level;
	}

	private static BiomeGenBase[] convertBiomeArray(byte[] biomeIds) {
		BiomeGenBase[] biomes = new BiomeGenBase[biomeIds.length];
		for (int i = 0; i < biomeIds.length; i++) {
			// Convert the byte to an unsigned int and fetch the corresponding Biome
			biomes[i] = BiomeGenBase.getBiome(biomeIds[i] & 0xFF);
			if (biomes[i] == null) {
				biomes[i] = BiomeGenBase.plains; // Default to Plains if the biome is not found
			}
		}
		return biomes;
	}

	@NotNull
	private static NibbleArray[] copyLightData(World level, ExtendedBlockStorage section) {
		var arrays = new NibbleArray[2];
		arrays[EnumSkyBlock.BLOCK.ordinal()] = copyLightArray(section, EnumSkyBlock.BLOCK);

		// Dimensions without sky-light should not have a default-initialized array
		if (!level.provider.getHasNoSky()) {
			arrays[EnumSkyBlock.SKY.ordinal()] = copyLightArray(section, EnumSkyBlock.SKY);
		}

		return arrays;
	}

	/**
	 * Copies the light data array for the given light type for this chunk, or
	 * returns a default-initialized value if the light array is not loaded.
	 */
	@NotNull
	private static NibbleArray copyLightArray(ExtendedBlockStorage section, EnumSkyBlock type) {
		NibbleArray array;

		if (section != null) {
			array = switch (type) {
			case SKY -> section.getSkylightArray();
			case BLOCK -> section.getBlocklightArray();
			};
		} else {
			array = null;
		}

		if (array == null) {
			array = switch (type) {
			case SKY -> DEFAULT_SKY_LIGHT_ARRAY;
			case BLOCK -> DEFAULT_BLOCK_LIGHT_ARRAY;
			};
		}

		return array;
	}

	private static final BlockPos.MutableBlockPos scratchPos = new BlockPos.MutableBlockPos();

	@Nullable
	private static Int2ReferenceMap<TileEntity> copyBlockEntities(Chunk chunk, SectionPos pos) {
		Int2ReferenceOpenHashMap<TileEntity> blockEntities = new Int2ReferenceOpenHashMap<>();

		for (int y = pos.minBlockY(); y <= pos.maxBlockY(); y++) {
			for (int z = pos.minBlockZ(); z <= pos.maxBlockZ(); z++) {
				for (int x = pos.minBlockX(); x <= pos.maxBlockX(); x++) {
					scratchPos.set(x, y, z);

					Block block = chunk.getBlock(scratchPos);

					if (block.hasTileEntity()) {
						TileEntity blockEntity = chunk.getTileEntity(scratchPos, Chunk.EnumCreateEntityType.IMMEDIATE);

						if (blockEntity != null) {
							blockEntities.put(LevelSlice.getLocalBlockIndex(x & 15, y & 15, z & 15), blockEntity);
						}
					}
				}
			}
		}

		return blockEntities;
	}

	public SectionPos getPosition() {
		return this.pos;
	}

	public @Nullable IBlockState[] getBlockData() {
		if (this.section == null)
			return null;
		if (this.section.getData() == null)
			return null;

		IBlockState[] blockData = new IBlockState[4096];

		for (int i = 0; i < this.section.getData().length; i++) {
			blockData[i] = Block.BLOCK_STATE_IDS.getByValue(this.section.getData()[i]);
		}

		return blockData;
	}

	public @Nullable BiomeGenBase[] getBiomeData() {
		return this.biomeData;
	}

	public @Nullable Int2ReferenceMap<TileEntity> getBlockEntityMap() {
		return this.blockEntityMap;
	}

	public @Nullable NibbleArray getLightArray(EnumSkyBlock type) {
		if (section == null)
			return null;

		if (type == EnumSkyBlock.SKY) {
			return (!level.provider.getHasNoSky() && section.getSkylightArray() != null) ? section.getSkylightArray()
					: null;
		}
		return section.getBlocklightArray();
	}

	public long getLastUsedTimestamp() {
		return this.lastUsedTimestamp;
	}

	public void setLastUsedTimestamp(long timestamp) {
		this.lastUsedTimestamp = timestamp;
	}

	public ExtendedBlockStorage getSection() {
		return section;
	}

	public Chunk getChunk() {
		return chunk;
	}
}
