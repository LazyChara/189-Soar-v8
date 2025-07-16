package com.soarclient.libraries.soarium.gl.arena.staging;

import java.nio.ByteBuffer;

import com.soarclient.libraries.soarium.gl.buffer.GlBuffer;
import com.soarclient.libraries.soarium.gl.buffer.GlBufferUsage;
import com.soarclient.libraries.soarium.gl.buffer.GlMutableBuffer;
import com.soarclient.libraries.soarium.gl.device.CommandList;

public class FallbackStagingBuffer implements StagingBuffer {
	private final GlMutableBuffer fallbackBufferObject;

	public FallbackStagingBuffer(CommandList commandList) {
		this.fallbackBufferObject = commandList.createMutableBuffer();
	}

	@Override
	public void enqueueCopy(CommandList commandList, ByteBuffer data, GlBuffer dst, long writeOffset) {
		commandList.uploadData(this.fallbackBufferObject, data, GlBufferUsage.STREAM_COPY);
		commandList.copyBufferSubData(this.fallbackBufferObject, dst, 0, writeOffset, data.remaining());
	}

	@Override
	public void flush(CommandList commandList) {
		commandList.allocateStorage(this.fallbackBufferObject, 0L, GlBufferUsage.STREAM_COPY);
	}

	@Override
	public void delete(CommandList commandList) {
		commandList.deleteBuffer(this.fallbackBufferObject);
	}

	@Override
	public void flip() {

	}

	@Override
	public String toString() {
		return "Fallback";
	}
}
