package com.soarclient.libraries.soarium.render.chunk.compile.tasks;

import org.joml.Vector3dc;

import com.soarclient.libraries.soarium.render.chunk.RenderSection;
import com.soarclient.libraries.soarium.render.chunk.compile.ChunkBuildContext;
import com.soarclient.libraries.soarium.render.chunk.compile.ChunkSortOutput;
import com.soarclient.libraries.soarium.render.chunk.compile.executor.ChunkBuilder;
import com.soarclient.libraries.soarium.render.chunk.translucent_sorting.data.DynamicData;
import com.soarclient.libraries.soarium.render.chunk.translucent_sorting.data.Sorter;
import com.soarclient.libraries.soarium.util.task.CancellationToken;

import net.minecraft.client.Minecraft;
import net.minecraft.profiler.Profiler;

public class ChunkBuilderSortingTask extends ChunkBuilderTask<ChunkSortOutput> {
	private final Sorter sorter;

	public ChunkBuilderSortingTask(RenderSection render, int frame, Vector3dc absoluteCameraPos, Sorter sorter) {
		super(render, frame, absoluteCameraPos);
		this.sorter = sorter;
	}

	@Override
	public ChunkSortOutput execute(ChunkBuildContext context, CancellationToken cancellationToken) {
		if (cancellationToken.isCancelled()) {
			return null;
		}

		Profiler profiler = Minecraft.getMinecraft().mcProfiler;
		profiler.startSection("translucency sorting");

		this.sorter.writeIndexBuffer(this, false);

		profiler.endSection();
		return new ChunkSortOutput(this.render, this.submitTime, this.sorter);
	}

	public static ChunkBuilderSortingTask createTask(RenderSection render, int frame, Vector3dc absoluteCameraPos) {
		if (render.getTranslucentData() instanceof DynamicData dynamicData) {
			return new ChunkBuilderSortingTask(render, frame, absoluteCameraPos, dynamicData.getSorter());
		}
		return null;
	}

	@Override
	public int getEffort() {
		return ChunkBuilder.LOW_EFFORT;
	}
}
