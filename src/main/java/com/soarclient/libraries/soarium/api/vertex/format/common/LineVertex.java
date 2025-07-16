package com.soarclient.libraries.soarium.api.vertex.format.common;

import com.soarclient.libraries.soarium.api.vertex.attributes.common.ColorAttribute;
import com.soarclient.libraries.soarium.api.vertex.attributes.common.NormalAttribute;
import com.soarclient.libraries.soarium.api.vertex.attributes.common.PositionAttribute;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;

public final class LineVertex {
	public static final VertexFormat FORMAT = DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL;

	public static final int STRIDE = 20;

	private static final int OFFSET_POSITION = 0;
	private static final int OFFSET_COLOR = 12;
	private static final int OFFSET_NORMAL = 16;

	public static void put(long ptr, float x, float y, float z, int color, int normal) {
		PositionAttribute.put(ptr + OFFSET_POSITION, x, y, z);
		ColorAttribute.set(ptr + OFFSET_COLOR, color);
		NormalAttribute.set(ptr + OFFSET_NORMAL, normal);
	}
}
