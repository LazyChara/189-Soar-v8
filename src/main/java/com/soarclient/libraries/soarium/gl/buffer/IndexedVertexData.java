package com.soarclient.libraries.soarium.gl.buffer;

import com.soarclient.libraries.soarium.gl.attribute.GlVertexFormat;
import com.soarclient.libraries.soarium.util.NativeBuffer;

/**
 * Helper type for tagging the vertex format alongside the raw buffer data.
 */
public record IndexedVertexData(GlVertexFormat vertexFormat, NativeBuffer vertexBuffer, NativeBuffer indexBuffer) {
	public void delete() {
		this.vertexBuffer.free();
		this.indexBuffer.free();
	}
}
