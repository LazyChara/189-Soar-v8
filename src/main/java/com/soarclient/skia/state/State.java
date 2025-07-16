package com.soarclient.skia.state;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

public class State {

	private final Map<PixelStoreParameter, Integer> pixelStores = new HashMap<>();
	private int lastActiveTexture = 0;
	private int lastProgram = 0;
	private int lastSampler = 0;
	private int lastVertexArray = 0;
	private int lastArrayBuffer = 0;

	private int lastBlendSrcRgb = 0;
	private int lastBlendDstRgb = 0;
	private int lastBlendSrcAlpha = 0;
	private int lastBlendDstAlpha = 0;
	private int lastBlendEquationRgb = 0;
	private int lastBlendEquationAlpha = 0;

	public void backupCurrentState() {
		GL11.glPushClientAttrib(GL11.GL_CLIENT_ALL_ATTRIB_BITS);
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

		lastActiveTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
		lastProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
		lastSampler = GL11.glGetInteger(GL33.GL_SAMPLER_BINDING);
		lastArrayBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);
		lastVertexArray = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);

		for (PixelStoreParameter parameter : PixelStoreParameter.values()) {
			pixelStores.put(parameter, GL11.glGetInteger(parameter.getValue()));
		}

		lastBlendSrcRgb = GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB);
		lastBlendDstRgb = GL11.glGetInteger(GL14.GL_BLEND_DST_RGB);
		lastBlendSrcAlpha = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA);
		lastBlendDstAlpha = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);
		lastBlendEquationRgb = GL11.glGetInteger(GL20.GL_BLEND_EQUATION_RGB);
		lastBlendEquationAlpha = GL11.glGetInteger(GL20.GL_BLEND_EQUATION_ALPHA);
	}

	public void restorePreviousState() {
		GL11.glPopAttrib();
		GL11.glPopClientAttrib();

		GL20.glUseProgram(lastProgram);
		GL33.glBindSampler(0, lastSampler);
		GL13.glActiveTexture(lastActiveTexture);
		GL30.glBindVertexArray(lastVertexArray);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, lastArrayBuffer);
		GL20.glBlendEquationSeparate(lastBlendEquationRgb, lastBlendEquationAlpha);
		GL14.glBlendFuncSeparate(lastBlendSrcRgb, lastBlendDstRgb, lastBlendSrcAlpha, lastBlendDstAlpha);

		for (PixelStoreParameter parameter : PixelStoreParameter.values()) {
			GL11.glPixelStorei(parameter.getValue(), pixelStores.get(parameter));
		}
	}
}