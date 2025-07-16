package com.soarclient.libraries.soarium.render.chunk.shader;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.soarclient.libraries.soarium.gl.shader.uniform.GlUniform;
import com.soarclient.libraries.soarium.gl.shader.uniform.GlUniformBlock;

import java.util.function.IntFunction;

public interface ShaderBindingContext {
	@NotNull
	<U extends GlUniform<?>> U bindUniform(String name, IntFunction<U> factory);

	@Nullable
	<U extends GlUniform<?>> U bindUniformOptional(String name, IntFunction<U> factory);

	@NotNull
	GlUniformBlock bindUniformBlock(String name, int bindingPoint);

	@Nullable
	GlUniformBlock bindUniformBlockOptional(String name, int bindingPoint);
}
