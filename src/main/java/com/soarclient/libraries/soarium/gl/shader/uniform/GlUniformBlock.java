package com.soarclient.libraries.soarium.gl.shader.uniform;

import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;

import com.soarclient.libraries.soarium.gl.buffer.GlBuffer;

public class GlUniformBlock {
	private final int binding;

	public GlUniformBlock(int uniformBlockBinding) {
		this.binding = uniformBlockBinding;
	}

	public void bindBuffer(GlBuffer buffer) {
		GL30.glBindBufferBase(GL31.GL_UNIFORM_BUFFER, this.binding, buffer.handle());
	}
}
