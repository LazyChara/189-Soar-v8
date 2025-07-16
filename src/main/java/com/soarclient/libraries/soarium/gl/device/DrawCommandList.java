package com.soarclient.libraries.soarium.gl.device;

import com.soarclient.libraries.soarium.gl.tessellation.GlIndexType;

public interface DrawCommandList extends AutoCloseable {
	void multiDrawElementsBaseVertex(MultiDrawBatch batch, GlIndexType indexType);

	void endTessellating();

	void flush();

	@Override
	default void close() {
		this.flush();
	}
}
