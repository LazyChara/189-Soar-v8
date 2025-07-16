package com.soarclient.libraries.soarium.render.chunk.vertex.format;

import com.soarclient.libraries.soarium.gl.attribute.GlVertexFormat;

public interface ChunkVertexType {
	GlVertexFormat getVertexFormat();

	ChunkVertexEncoder getEncoder();
}
