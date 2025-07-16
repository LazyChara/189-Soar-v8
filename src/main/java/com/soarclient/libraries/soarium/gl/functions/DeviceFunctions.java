package com.soarclient.libraries.soarium.gl.functions;

import com.soarclient.libraries.soarium.gl.device.RenderDevice;

public class DeviceFunctions {
	private final BufferStorageFunctions bufferStorageFunctions;

	public DeviceFunctions(RenderDevice device) {
		this.bufferStorageFunctions = BufferStorageFunctions.pickBest(device);
	}

	public BufferStorageFunctions getBufferStorageFunctions() {
		return this.bufferStorageFunctions;
	}
}
