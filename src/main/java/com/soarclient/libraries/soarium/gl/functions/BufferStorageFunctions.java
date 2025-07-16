package com.soarclient.libraries.soarium.gl.functions;

import org.lwjgl.opengl.ARBBufferStorage;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL44;

import com.soarclient.libraries.soarium.gl.buffer.GlBufferStorageFlags;
import com.soarclient.libraries.soarium.gl.buffer.GlBufferTarget;
import com.soarclient.libraries.soarium.gl.device.RenderDevice;
import com.soarclient.libraries.soarium.gl.util.EnumBitField;

public enum BufferStorageFunctions {
	NONE {
		@Override
		public void createBufferStorage(GlBufferTarget target, long length, EnumBitField<GlBufferStorageFlags> flags) {
			throw new UnsupportedOperationException();
		}
	},
	CORE {
		@Override
		public void createBufferStorage(GlBufferTarget target, long length, EnumBitField<GlBufferStorageFlags> flags) {
			GL44.glBufferStorage(target.getTargetParameter(), length, flags.getBitField());
		}
	},
	ARB {
		@Override
		public void createBufferStorage(GlBufferTarget target, long length, EnumBitField<GlBufferStorageFlags> flags) {
			ARBBufferStorage.glBufferStorage(target.getTargetParameter(), length, flags.getBitField());
		}
	};

	public static BufferStorageFunctions pickBest(RenderDevice device) {
		ContextCapabilities capabilities = device.getCapabilities();

		if (capabilities.OpenGL44) {
			return CORE;
		} else if (capabilities.GL_ARB_buffer_storage) {
			return ARB;
		} else {
			return NONE;
		}
	}

	public abstract void createBufferStorage(GlBufferTarget target, long length,
			EnumBitField<GlBufferStorageFlags> flags);
}
