package com.soarclient.libraries.soarium.gl.tessellation;

import com.soarclient.libraries.soarium.gl.attribute.GlVertexAttributeBinding;
import com.soarclient.libraries.soarium.gl.buffer.GlBuffer;
import com.soarclient.libraries.soarium.gl.buffer.GlBufferTarget;

public record TessellationBinding(GlBufferTarget target, GlBuffer buffer,
		GlVertexAttributeBinding[] attributeBindings) {
	public static TessellationBinding forVertexBuffer(GlBuffer buffer, GlVertexAttributeBinding[] attributes) {
		return new TessellationBinding(GlBufferTarget.ARRAY_BUFFER, buffer, attributes);
	}

	public static TessellationBinding forElementBuffer(GlBuffer buffer) {
		return new TessellationBinding(GlBufferTarget.ELEMENT_BUFFER, buffer, new GlVertexAttributeBinding[0]);
	}
}
