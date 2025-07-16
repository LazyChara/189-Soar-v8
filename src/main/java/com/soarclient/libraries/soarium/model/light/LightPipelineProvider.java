package com.soarclient.libraries.soarium.model.light;

import java.util.EnumMap;

import com.soarclient.libraries.soarium.model.light.data.LightDataAccess;
import com.soarclient.libraries.soarium.model.light.flat.FlatLightPipeline;
import com.soarclient.libraries.soarium.model.light.smooth.SmoothLightPipeline;

public class LightPipelineProvider {
	private final EnumMap<LightMode, LightPipeline> lighters = new EnumMap<>(LightMode.class);

	public LightPipelineProvider(LightDataAccess cache) {
		this.lighters.put(LightMode.SMOOTH, new SmoothLightPipeline(cache));
		this.lighters.put(LightMode.FLAT, new FlatLightPipeline(cache));
	}

	public LightPipeline getLighter(LightMode type) {
		LightPipeline pipeline = this.lighters.get(type);

		if (pipeline == null) {
			throw new NullPointerException("No lighter exists for mode: " + type.name());
		}

		return pipeline;
	}
}
