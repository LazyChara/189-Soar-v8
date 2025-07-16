package com.soarclient.libraries.soarium.util;

import java.util.Arrays;
import net.minecraft.util.EnumFacing;

/**
 * Contains a number of cached arrays to avoid allocations since calling
 * Enum#values() requires the backing array to be cloned every time.
 */
public class DirectionUtil {
	public static final EnumFacing[] ALL_DIRECTIONS = EnumFacing.values();

	// Provides the same order as enumerating Direction and checking the axis of
	// each value
	public static final EnumFacing[] HORIZONTAL_DIRECTIONS = new EnumFacing[] { EnumFacing.NORTH, EnumFacing.SOUTH,
			EnumFacing.WEST, EnumFacing.EAST };

	private static final EnumFacing[] OPPOSITE_DIRECTIONS = Arrays.stream(ALL_DIRECTIONS).map(EnumFacing::getOpposite)
			.toArray(EnumFacing[]::new);

	// Direction#byId is slow in the absence of Lithium
	public static EnumFacing getOpposite(EnumFacing dir) {
		return OPPOSITE_DIRECTIONS[dir.ordinal()];
	}
}
