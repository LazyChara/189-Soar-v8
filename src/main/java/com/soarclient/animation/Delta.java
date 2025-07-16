package com.soarclient.animation;

import org.lwjgl.Sys;

import com.soarclient.event.impl.GameLoopEventListener;

public class Delta implements GameLoopEventListener {

	private static double deltaTime;
	private long lastFrame;

	@Override
	public void onGameLoop() {
		final long currentTime = (Sys.getTime() * 1000) / Sys.getTimerResolution();
		final double deltaTime = (int) (currentTime - lastFrame);
		lastFrame = currentTime;
		Delta.deltaTime = deltaTime;
	}

	public static double getDeltaTime() {
		return deltaTime;
	}
}
