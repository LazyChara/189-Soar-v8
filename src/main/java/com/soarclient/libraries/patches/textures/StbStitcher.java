package com.soarclient.libraries.patches.textures;

import org.lwjgl.stb.STBRPContext;
import org.lwjgl.stb.STBRPNode;
import org.lwjgl.stb.STBRPRect;
import org.lwjgl.stb.STBRectPack;

import com.soarclient.utils.tuples.Pair;

import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraft.util.MathHelper;

public class StbStitcher {

	public static Pair<Integer, Integer> packRects(Stitcher.Holder[] holders) {

		int holderSize = holders.length;

		try (STBRPRect.Buffer rectBuf = STBRPRect.malloc(holderSize); STBRPContext ctx = STBRPContext.malloc();) {

			int sqSize = 0;
			int maxW = 16, maxH = 16;
			for (int j = 0; j < holderSize; ++j) {
				Stitcher.Holder holder = holders[j];

				int width = holder.getWidth();
				int height = holder.getHeight();

				rectBuf.get(j).set(j, width, height, 0, 0, false);

				sqSize += width * height;
				maxW = Math.max(maxW, width);
				maxH = Math.max(maxH, height);
			}

			maxW = MathHelper.roundUpToPowerOfTwo(maxW);
			maxH = MathHelper.roundUpToPowerOfTwo(maxH);
			final int guessedSqSide = MathHelper.roundUpToPowerOfTwo((int) Math.ceil(Math.sqrt(sqSize)));
			int atlasWidth = Math.max(guessedSqSide, maxW * 2);
			int atlasHeight = Math.max(guessedSqSide, maxH * 2);

			boolean doubleX = true;
			for (int retries = 0; retries < 10; retries++) {
				try (STBRPNode.Buffer nodes = STBRPNode.malloc(atlasWidth + 32)) {

					STBRectPack.stbrp_init_target(ctx, atlasWidth, atlasHeight, nodes);

					if (STBRectPack.stbrp_pack_rects(ctx, rectBuf) == 0) {

						if (doubleX) {
							atlasWidth *= 2;
						} else {
							atlasHeight *= 2;
						}
						doubleX = !doubleX;
						continue;
					}

					for (STBRPRect rect : rectBuf) {
						Stitcher.Holder holder = holders[rect.id()];

						if (!rect.was_packed()) {
							throw new StitcherException(holder, "Could not fit " + holder.getAtlasSprite().getIconName()
									+ " into " + atlasWidth + "x" + atlasHeight + " atlas!");
						}

						holder.getAtlasSprite().initSprite(atlasWidth, atlasHeight, rect.x(), rect.y(), false);
					}

					return Pair.of(atlasWidth, atlasHeight);
				}
			}
			throw new IllegalStateException(
					"Could not fit all sprites into an atlas of size " + atlasWidth + "x" + atlasHeight);
		}
	}
}
