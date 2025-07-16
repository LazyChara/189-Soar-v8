package com.soarclient.libraries.soarium.util;

import net.minecraft.util.MathHelper;

public class CompactSineLUT {

	private static final int[] SINE_TABLE_INT = new int[16384 + 1];
	private static final float SINE_TABLE_MIDPOINT;

	static {
		for (int i = 0; i < SINE_TABLE_INT.length; i++) {
			SINE_TABLE_INT[i] = Float.floatToRawIntBits(MathHelper.SIN_TABLE[i]);
		}

		SINE_TABLE_MIDPOINT = MathHelper.SIN_TABLE[MathHelper.SIN_TABLE.length / 2];

		for (int i = 0; i < MathHelper.SIN_TABLE.length; i++) {
			float expected = MathHelper.SIN_TABLE[i];
			float value = lookup(i);

			if (expected != value) {
				throw new IllegalArgumentException(
						String.format("LUT error at index %d (expected: %s, found: %s)", i, expected, value));
			}
		}
	}

	public static float sin(float f) {
		return lookup((int) (f * 10430.378f) & 0xFFFF);
	}

	public static float cos(float f) {
		return lookup((int) (f * 10430.378f + 16384.0f) & 0xFFFF);
	}

	private static float lookup(int index) {

		if (index == 32768) {
			return SINE_TABLE_MIDPOINT;
		}

		int neg = (index & 0x8000) << 16;

		int mask = (index << 17) >> 31;

		int pos = (0x8001 & mask) + (index ^ mask);

		pos &= 0x7fff;

		return Float.intBitsToFloat(SINE_TABLE_INT[pos] ^ neg);
	}
}