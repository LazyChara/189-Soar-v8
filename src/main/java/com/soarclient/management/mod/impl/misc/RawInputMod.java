package com.soarclient.management.mod.impl.misc;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.Display;

import com.soarclient.management.mod.Mod;
import com.soarclient.management.mod.ModCategory;
import com.soarclient.skia.font.Icon;

public class RawInputMod extends Mod {

	public RawInputMod() {
		super("mod.rawinput.name", "mod.rawinput.description", Icon.DRAG_PAN, ModCategory.MISC);
	}

	@Override
	public void onEnable() {
		if (GLFW.glfwRawMouseMotionSupported()) {
			GLFW.glfwSetInputMode(Display.getHandle(), GLFW.GLFW_RAW_MOUSE_MOTION, GLFW.GLFW_TRUE);
		} else {
			this.setEnabled(false);
		}
	}

	@Override
	public void onDisable() {
		GLFW.glfwSetInputMode(Display.getHandle(), GLFW.GLFW_RAW_MOUSE_MOTION, GLFW.GLFW_FALSE);
	}

}
