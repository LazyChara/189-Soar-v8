package com.soarclient.shaders;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;

public class ShaderUtils {

	private static Minecraft mc = Minecraft.getMinecraft();

	public static Framebuffer createFramebuffer(Framebuffer framebuffer) {
		if (framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth
				|| framebuffer.framebufferHeight != mc.displayHeight) {
			if (framebuffer != null) {
				framebuffer.deleteFramebuffer();
			}
			return new Framebuffer(mc.displayWidth, mc.displayHeight, true);
		}
		return framebuffer;
	}

	public static float calculateGaussianValue(float x, float sigma) {
		double PI = 3.141592653;
		double output = 1.0 / Math.sqrt(2.0 * PI * (sigma * sigma));
		return (float) (output * Math.exp(-(x * x) / (2.0 * (sigma * sigma))));
	}
}