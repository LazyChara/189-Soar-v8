package com.soarclient.libraries.soarium.gl.shader;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.soarclient.libraries.soarium.gl.GlObject;

import java.util.Arrays;

/**
 * A compiled OpenGL shader object.
 */
public class GlShader extends GlObject {
	private static final Logger LOGGER = LogManager.getLogger(GlShader.class);

	private final ResourceLocation name;

	public GlShader(ShaderType type, ResourceLocation name, ShaderParser.ParsedShader parsedShader) {
		this.name = name;

		int handle = GL20.glCreateShader(type.id);
		ShaderWorkarounds.safeShaderSource(handle, parsedShader.src());
		GL20.glCompileShader(handle);

		String log = GL20.glGetShaderInfoLog(handle, 1000);

		if (!log.isEmpty()) {
			LOGGER.warn("Shader compilation log for {}: {}", this.name, log);
			LOGGER.warn("Include table: {}", Arrays.toString(parsedShader.includeIds()));
		}

		int result = OpenGlHelper.glGetShaderi(handle, GL20.GL_COMPILE_STATUS);

		if (result != GL11.GL_TRUE) {
			throw new RuntimeException("Shader compilation failed, see log for details");
		}

		this.setHandle(handle);
	}

	public ResourceLocation getName() {
		return this.name;
	}

	public void delete() {
		GL20.glDeleteShader(this.handle());

		this.invalidateHandle();
	}
}
