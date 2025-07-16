package com.soarclient.libraries.soarium.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class CameraUtils {
	private static final BlockPos.MutableBlockPos blockPosition = new BlockPos.MutableBlockPos();

	public static BlockPos getBlockPosition() {
		Vec3 rawPosition = Minecraft.getMinecraft().getRenderViewEntity().getPositionVector();
		blockPosition.set((int) rawPosition.xCoord, (int) rawPosition.yCoord, (int) rawPosition.zCoord);
		return blockPosition;
	}
}
