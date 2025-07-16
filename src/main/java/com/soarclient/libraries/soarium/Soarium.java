package com.soarclient.libraries.soarium;

import com.soarclient.libraries.soarium.config.SoariumConfig;
import com.soarclient.libraries.soarium.culling.SoariumEntityCulling;

public class Soarium {

	private static SoariumConfig config;

	public static void start() {
		config = SoariumConfig.load();
		SoariumEntityCulling.getInstance().start();
	}

	public static SoariumConfig getConfig() {
		return config;
	}
}
