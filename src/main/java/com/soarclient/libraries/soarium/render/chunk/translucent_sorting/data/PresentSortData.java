package com.soarclient.libraries.soarium.render.chunk.translucent_sorting.data;

import java.nio.IntBuffer;

import com.soarclient.libraries.soarium.util.NativeBuffer;

public interface PresentSortData {
	NativeBuffer getIndexBuffer();

	default IntBuffer getIntBuffer() {
		return this.getIndexBuffer().getDirectBuffer().asIntBuffer();
	}
}
