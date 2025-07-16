package com.soarclient.management.mod.impl.hud.minimap;

import java.util.concurrent.CompletableFuture;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class MapCapture {

	private byte[] miniMapColors = new byte[16384];
	private DynamicTexture texture;

	private Minecraft mc = Minecraft.getMinecraft();

	public MapCapture() {
		texture = new DynamicTexture(128, 128);

		for (int i = 0; i < texture.getTextureData().length; ++i) {
			texture.getTextureData()[i] = 0;
		}
	}

	public void update(World worldIn) {

		CompletableFuture.runAsync(() -> {

			int i = 1;
			int j = mc.thePlayer.getPosition().getX();
			int k = mc.thePlayer.getPosition().getZ();
			int l = MathHelper.floor_double(mc.thePlayer.posX - (double) j) / i + 64;
			int i1 = MathHelper.floor_double(mc.thePlayer.posZ - (double) k) / i + 64;
			int j1 = 128 / i;

			if (worldIn.provider.getHasNoSky()) {
				j1 /= 2;
			}

			for (int k1 = l - j1 + 1; k1 < l + j1; ++k1) {
				double d0 = 0.0D;

				for (int l1 = i1 - j1 - 1; l1 < i1 + j1; ++l1) {
					if (k1 >= 0 && l1 >= -1 && k1 < 128 && l1 < 128) {
						int i2 = k1 - l;
						int j2 = l1 - i1;
						boolean flag1 = i2 * i2 + j2 * j2 > (j1 - 2) * (j1 - 2);
						int k2 = (j / i + k1 - 64) * i;
						int l2 = (k / i + l1 - 64) * i;

						Multiset<MapColor> multiset = HashMultiset.<MapColor>create();
						Chunk chunk = worldIn.getChunkFromBlockCoords(new BlockPos(k2, 0, l2));

						if (!chunk.isEmpty()) {
							int i3 = k2 & 15;
							int j3 = l2 & 15;
							int k3 = 0;
							double d1 = 0.0D;

							if (worldIn.provider.getHasNoSky()) {
								int l3 = k2 + l2 * 231871;
								l3 = l3 * l3 * 31287121 + l3 * 11;

								if ((l3 >> 20 & 1) == 0) {
									multiset.add(Blocks.dirt.getMapColor(Blocks.dirt.getDefaultState()
											.withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT)), 10);
								} else {
									multiset.add(Blocks.stone.getMapColor(Blocks.stone.getDefaultState()
											.withProperty(BlockStone.VARIANT, BlockStone.EnumType.STONE)), 100);
								}

								d1 = 100.0D;
							} else {
								BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

								for (int i4 = 0; i4 < i; ++i4) {
									for (int j4 = 0; j4 < i; ++j4) {
										int k4 = chunk.getHeightValue(i4 + i3, j4 + j3) + 1;
										IBlockState iblockstate = Blocks.air.getDefaultState();

										if (k4 > 1) {
											label541: {
												while (true) {
													--k4;
													iblockstate = chunk.getBlockState(
															blockpos$mutableblockpos.set(i4 + i3, k4, j4 + j3));

													if (iblockstate.getBlock()
															.getMapColor(iblockstate) != MapColor.airColor || k4 <= 0) {
														break;
													}
												}

												if (k4 > 0 && iblockstate.getBlock().getMaterial().isLiquid()) {
													int l4 = k4 - 1;

													while (true) {
														Block block = chunk.getBlock(i4 + i3, l4--, j4 + j3);
														++k3;

														if (l4 <= 0 || !block.getMaterial().isLiquid()) {
															break label541;
														}
													}
												}
											}
										}

										d1 += (double) k4 / (double) (i * i);
										MapColor mapColor = iblockstate.getBlock().getMapColor(iblockstate);
										multiset.add(mapColor);
									}
								}
							}

							k3 = k3 / (i * i);
							double d2 = (d1 - d0) * 4.0D / (double) (i + 4) + ((double) (k1 + l1 & 1) - 0.5D) * 0.4D;
							int i5 = 1;

							if (d2 > 0.6D) {
								i5 = 2;
							}

							if (d2 < -0.6D) {
								i5 = 0;
							}

							MapColor mapcolor = Iterables.getFirst(Multisets.copyHighestCountFirst(multiset),
									MapColor.airColor);

							if (mapcolor == MapColor.waterColor) {
								d2 = (double) k3 * 0.1D + (double) (k1 + l1 & 1) * 0.2D;
								i5 = 1;

								if (d2 < 0.5D) {
									i5 = 2;
								}

								if (d2 > 0.9D) {
									i5 = 0;
								}
							}

							d0 = d1;

							if (l1 >= 0 && i2 * i2 + j2 * j2 < j1 * j1 && (!flag1 || (k1 + l1 & 1) != 0)) {
								byte b0 = miniMapColors[k1 + l1 * 128];
								byte b1 = (byte) (mapcolor.colorIndex * 4 + i5);

								if (b0 != b1) {
									miniMapColors[k1 + l1 * 128] = b1;
								}
							}
						}
					}
				}
			}

			for (int i32 = 0; i32 < 16384; i32++) {
				int j32 = miniMapColors[i32] & 255;

				if (j32 / 4 == 0) {
					texture.getTextureData()[i32] = (i32 + i32 / 128 & 1) * 8 + 16 << 24;
				} else {
					texture.getTextureData()[i32] = MapColor.mapColorArray[j32 / 4].getMapColor(j32 & 3);
				}
			}

			Minecraft.getMinecraft().addScheduledTask(() -> {
				try {
					texture.updateDynamicTexture();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		});
	}

	public DynamicTexture getTexture() {
		return texture;
	}
}