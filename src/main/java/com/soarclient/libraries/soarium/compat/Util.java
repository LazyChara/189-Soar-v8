package com.soarclient.libraries.soarium.compat;

import java.util.function.Consumer;
import net.minecraft.util.Vec3;

public class Util {
	public static <T> T make(T t, Consumer<? super T> consumer) {
		consumer.accept(t);
		return t;
	}

	public static Vec3 scale(Vec3 self, double d) {
		return new Vec3(self.xCoord * d, self.yCoord * d, self.zCoord * d);
	}

	public static Vec3 fromRGB24(int n) {
		double d = (double) (n >> 16 & 0xFF) / 255.0;
		double d2 = (double) (n >> 8 & 0xFF) / 255.0;
		double d3 = (double) (n & 0xFF) / 255.0;
		return new Vec3(d, d2, d3);
	}
}
