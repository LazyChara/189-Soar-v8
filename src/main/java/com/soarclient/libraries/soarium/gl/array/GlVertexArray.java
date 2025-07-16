package com.soarclient.libraries.soarium.gl.array;

import org.lwjgl.opengl.GL30;

import com.soarclient.libraries.soarium.gl.GlObject;

/**
 * Provides Vertex Array functionality on supported platforms.
 */
public class GlVertexArray extends GlObject {
	public static final int NULL_ARRAY_ID = 0;

	public GlVertexArray() {
		this.setHandle(GL30.glGenVertexArrays());
	}
}
