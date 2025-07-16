package com.soarclient.libraries.soarium.compat.minecraft.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class FogHelper {
	public static float getFogEnd() {
		return GlStateManager.fogState.end;
	}

	public static float getFogStart() {
		return GlStateManager.fogState.start;
	}

	public static float[] getFogColor() {
		EntityRenderer gameRenderer = Minecraft.getMinecraft().entityRenderer;
		return new float[] { gameRenderer.fogColorRed, gameRenderer.fogColorGreen, gameRenderer.fogColorBlue, 1.0f };
	}
}