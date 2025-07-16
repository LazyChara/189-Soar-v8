package com.soarclient.libraries.soarium.api.vertex.format.common;

import com.soarclient.libraries.soarium.api.vertex.attributes.common.ColorAttribute;
import com.soarclient.libraries.soarium.api.vertex.attributes.common.LightAttribute;
import com.soarclient.libraries.soarium.api.vertex.attributes.common.PositionAttribute;
import com.soarclient.libraries.soarium.api.vertex.attributes.common.TextureAttribute;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;

public final class GlyphVertex {
	public static final VertexFormat FORMAT = DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL;

	public static final int STRIDE = 28;

	private static final int OFFSET_POSITION = 0;
	private static final int OFFSET_COLOR = 12;
	private static final int OFFSET_TEXTURE = 16;
	private static final int OFFSET_LIGHT = 24;

	public static void put(long ptr, float x, float y, float z, int color, float u, float v, int light) {
		PositionAttribute.put(ptr + OFFSET_POSITION, x, y, z);
		ColorAttribute.set(ptr + OFFSET_COLOR, color);
		TextureAttribute.put(ptr + OFFSET_TEXTURE, u, v);
		LightAttribute.set(ptr + OFFSET_LIGHT, light);
	}
}
