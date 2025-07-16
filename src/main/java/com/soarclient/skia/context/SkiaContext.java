package com.soarclient.skia.context;

import java.util.function.Consumer;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import com.soarclient.skia.state.UIState;

import io.github.humbleui.skija.BackendRenderTarget;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.ColorSpace;
import io.github.humbleui.skija.DirectContext;
import io.github.humbleui.skija.Surface;
import io.github.humbleui.skija.SurfaceColorFormat;
import io.github.humbleui.skija.SurfaceOrigin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class SkiaContext {

	private static DirectContext context = null;
	private static Surface surface;
	private static BackendRenderTarget renderTarget;

	public static Canvas getCanvas() {
		return surface.getCanvas();
	}

	public static void initialize() {
		if (context == null) {
			context = DirectContext.makeGL();
		}
	}

	public static void createSurface() {
		createSurface(Display.getWidth(), Display.getHeight());
	}

	public static void createSurface(int width, int height) {
		initialize();
		int framebufferObject = Minecraft.getMinecraft().getFramebuffer().framebufferObject;
		renderTarget = BackendRenderTarget.makeGL(width, height, 0, 8, framebufferObject, GL11.GL_RGBA8);
		surface = Surface.wrapBackendRenderTarget(context, renderTarget, SurfaceOrigin.BOTTOM_LEFT,
				SurfaceColorFormat.RGBA_8888, ColorSpace.getSRGB());
	}

	public static void preFlush() {
		UIState.backup();
		GlStateManager.clearColor(0f, 0f, 0f, 0f);
		if (context != null) {
			context.resetGLAll();
		}

		GL11.glDisable(GL11.GL_ALPHA_TEST);
	}

	public static void flush() {
		if (context != null) {
			context.flush();
		}
		UIState.restore();
	}

	public static void draw(Consumer<Canvas> drawingLogic) {
		preFlush();
		Canvas canvas = getCanvas();
		drawingLogic.accept(canvas);
		flush();
	}

	public static void onResize(int newWidth, int newHeight) {

		if (surface != null) {
			surface.close();
		}

		if (renderTarget != null) {
			renderTarget.close();
		}
		createSurface(newWidth, newHeight);
	}

	public static DirectContext getContext() {
		return context;
	}
}
