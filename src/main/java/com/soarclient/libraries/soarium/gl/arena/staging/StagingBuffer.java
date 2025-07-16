package com.soarclient.libraries.soarium.gl.arena.staging;

import java.nio.ByteBuffer;

import com.soarclient.libraries.soarium.gl.buffer.GlBuffer;
import com.soarclient.libraries.soarium.gl.device.CommandList;

public interface StagingBuffer {
	void enqueueCopy(CommandList commandList, ByteBuffer data, GlBuffer dst, long writeOffset);

	void flush(CommandList commandList);

	void delete(CommandList commandList);

	void flip();
}
