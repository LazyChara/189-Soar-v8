package com.soarclient.libraries.soarium.render.chunk.compile;

import com.soarclient.libraries.soarium.render.chunk.RenderSection;
import com.soarclient.libraries.soarium.render.chunk.translucent_sorting.data.DynamicTopoData;
import com.soarclient.libraries.soarium.render.chunk.translucent_sorting.data.SortData;
import com.soarclient.libraries.soarium.render.chunk.translucent_sorting.data.Sorter;
import com.soarclient.libraries.soarium.util.NativeBuffer;

public class ChunkSortOutput extends BuilderTaskOutput implements SortData {
	private NativeBuffer indexBuffer;
	private boolean reuseUploadedIndexData;
	private DynamicTopoData.DynamicTopoSorter topoSorter;

	public ChunkSortOutput(RenderSection render, int buildTime) {
		super(render, buildTime);
	}

	public ChunkSortOutput(RenderSection render, int buildTime, Sorter data) {
		this(render, buildTime);
		this.copyResultFrom(data);
	}

	public void copyResultFrom(Sorter sorter) {
		this.indexBuffer = sorter.getIndexBuffer();
		this.reuseUploadedIndexData = false;
		if (sorter instanceof DynamicTopoData.DynamicTopoSorter topoSorterInstance) {
			this.topoSorter = topoSorterInstance;
		}
	}

	public void markAsReusingUploadedData() {
		this.reuseUploadedIndexData = true;
	}

	@Override
	public NativeBuffer getIndexBuffer() {
		return this.indexBuffer;
	}

	@Override
	public boolean isReusingUploadedIndexData() {
		return this.reuseUploadedIndexData;
	}

	public DynamicTopoData.DynamicTopoSorter getTopoSorter() {
		return this.topoSorter;
	}

	@Override
	public void destroy() {
		super.destroy();

		if (this.indexBuffer != null) {
			this.indexBuffer.free();
		}
	}
}
