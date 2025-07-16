package com.soarclient.libraries.soarium.render.vertex;

import com.soarclient.libraries.soarium.gl.attribute.GlVertexAttributeFormat;

public record VertexFormatAttribute(String name, GlVertexAttributeFormat format, int count, boolean normalized,
		boolean intType) {

}