package com.soarclient.libraries.patches.liquid;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class LiquidHook {

	public static boolean cancelOldClientLiquid(Block isFull, World worldIn, BlockPos pos) {

		if (isFull == Blocks.air) {
			return false;
		} else {

			Material material = worldIn.getBlockState(pos).getBlock().getMaterial();
			boolean flag = !material.isSolid();

			if (!worldIn.isAirBlock(pos) && !flag) {
				return false;
			} else {
				if (worldIn.provider.doesWaterVaporize() && isFull == Blocks.flowing_water) {
					int i = pos.getX();
					int j = pos.getY();
					int k = pos.getZ();

					worldIn.playSoundEffect((double) ((float) i + 0.5F), (double) ((float) j + 0.5F),
							(double) ((float) k + 0.5F), "random.fizz", 0.5F,
							2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

					for (int l = 0; l < 8; ++l) {
						worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, (double) i + Math.random(),
								(double) j + Math.random(), (double) k + Math.random(), 0.0D, 0.0D, 0.0D, new int[0]);
					}
				} else {
					if (!worldIn.isRemote && flag && !material.isLiquid()) {
						worldIn.destroyBlock(pos, true);
					}

					if (!worldIn.isRemote) {
						worldIn.setBlockState(pos, isFull.getDefaultState(), 3);
					}
				}

				return true;
			}
		}
	}

	public static void placeClientLiquid(EntityPlayerSP player, WorldClient worldIn, ItemStack heldStack,
			BlockPos hitPos, EnumFacing side, Vec3 hitVec) {

		if (heldStack != null) {

			float f = (float) (hitVec.xCoord - (double) hitPos.getX());
			float f1 = (float) (hitVec.yCoord - (double) hitPos.getY());
			float f2 = (float) (hitVec.zCoord - (double) hitPos.getZ());
			C08PacketPlayerBlockPlacement packet = new C08PacketPlayerBlockPlacement(hitPos, side.getIndex(),
					player.inventory.getCurrentItem(), f, f1, f2);

			if (heldStack.getUnlocalizedName().equals("item.bucketLava")) {
				worldIn.setBlockState(new BlockPos((double) packet.getPlacedBlockOffsetX(),
						(double) packet.getPlacedBlockOffsetY(), (double) packet.getPlacedBlockOffsetZ()),
						Blocks.flowing_lava.getDefaultState());
			} else if (heldStack.getUnlocalizedName().equals("item.bucketWater")) {
				worldIn.setBlockState(new BlockPos((double) packet.getPlacedBlockOffsetX(),
						(double) packet.getPlacedBlockOffsetY(), (double) packet.getPlacedBlockOffsetZ()),
						Blocks.flowing_water.getDefaultState());
			}
		}
	}
}
