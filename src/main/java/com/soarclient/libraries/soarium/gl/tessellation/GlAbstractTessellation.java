package com.soarclient.libraries.soarium.gl.tessellation;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.soarclient.libraries.soarium.gl.attribute.GlVertexAttributeBinding;
import com.soarclient.libraries.soarium.gl.device.CommandList;

public abstract class GlAbstractTessellation implements GlTessellation {
	protected final GlPrimitiveType primitiveType;
	protected final TessellationBinding[] bindings;

	protected GlAbstractTessellation(GlPrimitiveType primitiveType, TessellationBinding[] bindings) {
		this.primitiveType = primitiveType;
		this.bindings = bindings;
	}

	@Override
	public GlPrimitiveType getPrimitiveType() {
		return this.primitiveType;
	}

	protected void bindAttributes(CommandList commandList) {
		for (TessellationBinding binding : this.bindings) {
			commandList.bindBuffer(binding.target(), binding.buffer());

			for (GlVertexAttributeBinding attrib : binding.attributeBindings()) {
				if (attrib.isIntType()) {
					GL30.glVertexAttribIPointer(attrib.getIndex(), attrib.getCount(), attrib.getFormat(),
							attrib.getStride(), attrib.getPointer());
				} else {
					GL20.glVertexAttribPointer(attrib.getIndex(), attrib.getCount(), attrib.getFormat(),
							attrib.isNormalized(), attrib.getStride(), attrib.getPointer());
				}
				GL20.glEnableVertexAttribArray(attrib.getIndex());
			}
		}
	}
}
