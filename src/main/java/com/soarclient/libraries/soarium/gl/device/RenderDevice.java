package com.soarclient.libraries.soarium.gl.device;

import org.lwjgl.opengl.ContextCapabilities;

import com.soarclient.libraries.soarium.gl.functions.DeviceFunctions;

public interface RenderDevice {
	RenderDevice INSTANCE = new GLRenderDevice();

	CommandList createCommandList();

	static void enterManagedCode() {
		RenderDevice.INSTANCE.makeActive();
	}

	static void exitManagedCode() {
		RenderDevice.INSTANCE.makeInactive();
	}

	void makeActive();

	void makeInactive();

	ContextCapabilities getCapabilities();

	DeviceFunctions getDeviceFunctions();
}
