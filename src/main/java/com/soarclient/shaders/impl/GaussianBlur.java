package com.soarclient.shaders.impl;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.soarclient.shaders.Shader;
import com.soarclient.shaders.ShaderLoader;
import com.soarclient.shaders.ShaderUtils;
import com.soarclient.utils.TimerUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;

public class GaussianBlur {

	private Minecraft mc = Minecraft.getMinecraft();

	private Shader blurShader = ShaderLoader.loadShader("gaussian", true, true);
	private boolean skiaMode;
	private Framebuffer framebuffer = new Framebuffer(1, 1, false);
	private Framebuffer secondFramebuffer = new Framebuffer(1, 1, false);
	private float[] cachedBuffer = new float[200];
	private float prevRadius;
	private TimerUtils updateTimer = new TimerUtils();

	public GaussianBlur(boolean skiaMode) {
		this.skiaMode = skiaMode;
	}

	private void setupUniforms(float dir1, float dir2, float radius, float quality) {
		quality = (float) Math.max(quality, 1.0);
		blurShader.setUniform1i("textureIn", 0);
		blurShader.setUniform1i("maskTexture", 16);
		blurShader.setUniform2f("texelSize", 1.0F / (float) (mc.displayWidth / quality),
				1.0F / (float) (mc.displayHeight / quality));
		blurShader.setUniform2f("direction", dir1, dir2);
		blurShader.setUniform1f("radius", radius);

		FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(256);

		for (int i = 0; i <= radius; i++) {
			if (radius != prevRadius) {

				float val = ShaderUtils.calculateGaussianValue(i, radius / 2);

				cachedBuffer[i] = val;
				weightBuffer.put(val);
			} else {
				weightBuffer.put(cachedBuffer[i]);
			}
		}

		prevRadius = radius;

		weightBuffer.rewind();
		blurShader.setUniform1fv("weights", weightBuffer);
	}

	public void draw(float radius) {

		GlStateManager.enableBlend();
		GlStateManager.color(1, 1, 1, 1);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

		framebuffer = ShaderUtils.createFramebuffer(framebuffer);
		secondFramebuffer = ShaderUtils.createFramebuffer(secondFramebuffer);

		if (updateTimer.delay(16)) {

			GlStateManager.enableBlend();
			GlStateManager.color(1, 1, 1, 1);
			OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

			framebuffer.framebufferClear();
			framebuffer.bindFramebuffer(true);
			blurShader.init();
			setupUniforms(1, 0, Math.min(radius, 25F), radius * 0.04F);

			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.getFramebuffer().framebufferTexture);

			blurShader.bind();
			framebuffer.unbindFramebuffer();
			blurShader.finish();

			secondFramebuffer.framebufferClear();
			secondFramebuffer.bindFramebuffer(true);
			blurShader.init();
			setupUniforms(0, 1, Math.min(radius, 25F), radius * 0.04F);

			GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebuffer.framebufferTexture);

			blurShader.bind();
			secondFramebuffer.unbindFramebuffer();
			blurShader.finish();

			updateTimer.reset();

			GlStateManager.color(1, 1, 1, 1);
			GlStateManager.bindTexture(0);
		}

		mc.getFramebuffer().bindFramebuffer(true);
		secondFramebuffer.bindFramebufferTexture();
		if (!skiaMode) {
			blurShader.bind();
		}
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.bindTexture(0);
	}

	public int getTexture() {
		return secondFramebuffer.framebufferTexture;
	}
}