package com.soarclient.libraries.soarium.gl.buffer;

import org.lwjgl.opengl.GL15;

import com.soarclient.libraries.soarium.gl.GlObject;

public abstract class GlBuffer extends GlObject {
	private GlBufferMapping activeMapping;

	protected GlBuffer() {
		this.setHandle(GL15.glGenBuffers());
	}

	public GlBufferMapping getActiveMapping() {
		return this.activeMapping;
	}

	public void setActiveMapping(GlBufferMapping mapping) {
		this.activeMapping = mapping;
	}
}
