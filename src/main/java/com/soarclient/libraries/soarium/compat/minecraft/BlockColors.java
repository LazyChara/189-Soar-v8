package com.soarclient.libraries.soarium.compat.minecraft;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockStem;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import org.jetbrains.annotations.Nullable;

/**
 * Taken from Minecraft 1.12.2
 */
public class BlockColors {
	private final ObjectIntIdentityMap<IBlockColor> mapBlockColors = new ObjectIntIdentityMap<>();
	private final Reference2ReferenceMap<Block, IBlockColor> blocksToColor;
	private static final IBlockColor DEFAULT_PROVIDER = (state, view, pos, tint) -> -1;

	public static BlockColors INSTANCE;

	static {
		init();
	}

	public BlockColors() {
		this.blocksToColor = new Reference2ReferenceOpenHashMap<>();
		this.blocksToColor.defaultReturnValue(DEFAULT_PROVIDER);
	}

	public static BlockColors init() {
		INSTANCE = new BlockColors();
		INSTANCE.registerBlockColorHandler((state, worldIn, pos, tintIndex) -> {
			BlockDoublePlant.EnumPlantType type = state.getValue(BlockDoublePlant.VARIANT);
			return worldIn != null && pos != null
					&& (type == BlockDoublePlant.EnumPlantType.GRASS || type == BlockDoublePlant.EnumPlantType.FERN)
							? BiomeColorHelper.getGrassColorAtPos(worldIn,
									state.getValue(BlockDoublePlant.HALF) == BlockDoublePlant.EnumBlockHalf.UPPER
											? pos.down()
											: pos)
							: -1;
		}, Blocks.double_plant);
		INSTANCE.registerBlockColorHandler((state, worldIn, pos, tintIndex) -> {
			if (worldIn != null && pos != null) {
				TileEntity tileentity = worldIn.getTileEntity(pos);

				if (tileentity instanceof TileEntityFlowerPot) {
					Item item = ((TileEntityFlowerPot) tileentity).getFlowerPotItem();
					IBlockState iblockstate = Block.getBlockFromItem(item).getDefaultState();
					return INSTANCE.colorMultiplier(iblockstate, worldIn, pos, tintIndex);
				} else {
					return -1;
				}
			} else {
				return -1;
			}
		}, Blocks.flower_pot);
		INSTANCE.registerBlockColorHandler((state, worldIn, pos, tintIndex) -> worldIn != null && pos != null
				? BiomeColorHelper.getGrassColorAtPos(worldIn, pos)
				: ColorizerGrass.getGrassColor(0.5D, 1.0D), Blocks.grass);
		INSTANCE.registerBlockColorHandler((state, worldIn, pos, tintIndex) -> {
			BlockPlanks.EnumType blockplanks$enumtype = state.getValue(BlockOldLeaf.VARIANT);
			if (blockplanks$enumtype == BlockPlanks.EnumType.SPRUCE) {
				return ColorizerFoliage.getFoliageColorPine();
			} else if (blockplanks$enumtype == BlockPlanks.EnumType.BIRCH) {
				return ColorizerFoliage.getFoliageColorBirch();
			} else {
				return worldIn != null && pos != null ? BiomeColorHelper.getFoliageColorAtPos(worldIn, pos)
						: ColorizerFoliage.getFoliageColorBasic();
			}
		}, Blocks.leaves);
		INSTANCE.registerBlockColorHandler((state, worldIn, pos, tintIndex) -> worldIn != null && pos != null
				? BiomeColorHelper.getFoliageColorAtPos(worldIn, pos)
				: ColorizerFoliage.getFoliageColorBasic(), Blocks.leaves2);
		INSTANCE.registerBlockColorHandler((state, worldIn, pos,
				tintIndex) -> worldIn != null && pos != null ? BiomeColorHelper.getWaterColorAtPos(worldIn, pos) : -1,
				Blocks.water, Blocks.flowing_water);
		INSTANCE.registerBlockColorHandler((state, worldIn, pos,
				tintIndex) -> worldIn != null && pos != null ? BiomeColorHelper.getGrassColorAtPos(worldIn, pos) : -1,
				Blocks.reeds);
		INSTANCE.registerBlockColorHandler((state, worldIn, pos, tintIndex) -> {
			int i = state.getValue(BlockStem.AGE);
			int j = i * 32;
			int k = 255 - i * 8;
			int l = i * 4;
			return j << 16 | k << 8 | l;
		}, Blocks.melon_stem, Blocks.pumpkin_stem);
		INSTANCE.registerBlockColorHandler((state, worldIn, pos, tintIndex) -> {
			if (worldIn != null && pos != null) {
				return BiomeColorHelper.getGrassColorAtPos(worldIn, pos);
			}
			return state.getValue(BlockTallGrass.TYPE) == BlockTallGrass.EnumType.DEAD_BUSH ? 0xffffff
					: ColorizerGrass.getGrassColor(0.5D, 1.0D);
		}, Blocks.tallgrass);
		INSTANCE.registerBlockColorHandler((state, worldIn, pos, tintIndex) -> worldIn != null && pos != null
				? BiomeColorHelper.getFoliageColorAtPos(worldIn, pos)
				: ColorizerFoliage.getFoliageColorBasic(), Blocks.vine);
		INSTANCE.registerBlockColorHandler(
				(state, worldIn, pos, tintIndex) -> worldIn != null && pos != null ? 0x208030 : 0x71c35c,
				Blocks.waterlily);
		return INSTANCE;
	}

	public int getColor(IBlockState p_189991_1_, World p_189991_2_, BlockPos p_189991_3_) {
		IBlockColor iblockcolor = this.mapBlockColors.getByValue(Block.getIdFromBlock(p_189991_1_.getBlock()));

		if (iblockcolor != null) {
			return iblockcolor.colorMultiplier(p_189991_1_, null, null, 0);
		} else {
			MapColor mapcolor = p_189991_1_.getBlock().getMapColor(p_189991_1_);
			return mapcolor != null ? mapcolor.colorValue : -1;
		}
	}

	public int colorMultiplier(IBlockState state, @Nullable IBlockAccess blockAccess, @Nullable BlockPos pos,
			int renderPass) {
		IBlockColor iblockcolor = this.mapBlockColors.getByValue(Block.getIdFromBlock(state.getBlock()));
		return iblockcolor == null ? -1 : iblockcolor.colorMultiplier(state, blockAccess, pos, renderPass);
	}

	public void registerBlockColorHandler(IBlockColor blockColor, Block... blocksIn) {
		synchronized (this.blocksToColor) {
			for (Block block : blocksIn) {
				if (blockColor != null)
					this.blocksToColor.put(block, blockColor);
			}
		}
		for (Block block : blocksIn) {
			this.mapBlockColors.put(blockColor, Block.getIdFromBlock(block));
		}
	}

	public IBlockColor getColorProvider(IBlockState state) {
		return blocksToColor.get(state.getBlock());
	}
}