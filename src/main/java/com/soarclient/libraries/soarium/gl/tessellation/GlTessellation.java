package com.soarclient.libraries.soarium.gl.tessellation;

import com.soarclient.libraries.soarium.gl.device.CommandList;

public interface GlTessellation {
	void delete(CommandList commandList);

	void bind(CommandList commandList);

	void unbind(CommandList commandList);

	GlPrimitiveType getPrimitiveType();
}
