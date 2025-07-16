package com.soarclient.libraries.soarium.render.chunk.compile;

import com.soarclient.libraries.soarium.render.chunk.RenderSection;

public abstract class BuilderTaskOutput {
	public final RenderSection render;
	public final int submitTime;

	public BuilderTaskOutput(RenderSection render, int buildTime) {
		this.render = render;
		this.submitTime = buildTime;
	}

	public void destroy() {
	}
}
