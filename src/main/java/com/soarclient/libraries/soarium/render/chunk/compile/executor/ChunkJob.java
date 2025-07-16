package com.soarclient.libraries.soarium.render.chunk.compile.executor;

import com.soarclient.libraries.soarium.render.chunk.compile.ChunkBuildContext;
import com.soarclient.libraries.soarium.util.task.CancellationToken;

public interface ChunkJob extends CancellationToken {
	void execute(ChunkBuildContext context);

	boolean isStarted();

	int getEffort();
}
