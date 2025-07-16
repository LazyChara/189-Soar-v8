package com.soarclient.libraries.soarium.culling;

import com.soarclient.libraries.occlusionculling.OcclusionCullingInstance;
import com.soarclient.libraries.soarium.Soarium;

public class SoariumEntityCulling {

	private static SoariumEntityCulling instance = new SoariumEntityCulling();
	public OcclusionCullingInstance culling;
	public static boolean enabled = true;
	public CullTask cullTask;
	private Thread cullThread;

	public int renderedBlockEntities = 0;
	public int skippedBlockEntities = 0;
	public int renderedEntities = 0;
	public int skippedEntities = 0;

	public void start() {
		culling = new OcclusionCullingInstance(Soarium.getConfig().culling.tracingDistance,
				new SoariumCullingDataProvider());
		cullTask = new CullTask(culling, Soarium.getConfig().culling.blockEntityWhitelist);

		cullThread = new Thread(cullTask, "CullThread");

		cullThread.setUncaughtExceptionHandler((thread, ex) -> {
			ex.printStackTrace();
		});

		cullThread.start();
	}

	public void worldTick() {
		cullTask.requestCull = true;
	}

	public void clientTick() {
		cullTask.requestCull = true;
	}

	public static SoariumEntityCulling getInstance() {
		return instance;
	}
}
