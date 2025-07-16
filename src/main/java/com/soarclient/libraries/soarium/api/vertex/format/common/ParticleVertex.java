package com.soarclient.libraries.soarium.api.vertex.format.common;

import com.soarclient.libraries.soarium.api.vertex.attributes.common.ColorAttribute;
import com.soarclient.libraries.soarium.api.vertex.attributes.common.LightAttribute;
import com.soarclient.libraries.soarium.api.vertex.attributes.common.PositionAttribute;
import com.soarclient.libraries.soarium.api.vertex.attributes.common.TextureAttribute;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;

public final class ParticleVertex {
	public static final VertexFormat FORMAT = DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP;

	public static final int STRIDE = 28;

	private static final int OFFSET_POSITION = 0;
	private static final int OFFSET_TEXTURE = 12;
	private static final int OFFSET_COLOR = 20;
	private static final int OFFSET_LIGHT = 24;

	public static void put(long ptr, float x, float y, float z, float u, float v, int color, int light) {
		PositionAttribute.put(ptr + OFFSET_POSITION, x, y, z);
		TextureAttribute.put(ptr + OFFSET_TEXTURE, u, v);
		ColorAttribute.set(ptr + OFFSET_COLOR, color);
		LightAttribute.set(ptr + OFFSET_LIGHT, light);
	}
}
