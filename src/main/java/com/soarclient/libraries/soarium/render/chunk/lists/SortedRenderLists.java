package com.soarclient.libraries.soarium.render.chunk.lists;

import com.soarclient.libraries.soarium.util.iterator.ReversibleObjectArrayIterator;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * Stores one render list of sections per region, sorted by the order in which
 * they were discovered in the BFS of the occlusion culler. It also generates
 * render lists for sections of previously unseen regions.
 */
public class SortedRenderLists implements ChunkRenderListIterable {
	private static final SortedRenderLists EMPTY = new SortedRenderLists(ObjectArrayList.of());

	private final ObjectArrayList<ChunkRenderList> lists;

	SortedRenderLists(ObjectArrayList<ChunkRenderList> lists) {
		this.lists = lists;
	}

	@Override
	public ReversibleObjectArrayIterator<ChunkRenderList> iterator(boolean reverse) {
		return new ReversibleObjectArrayIterator<>(this.lists, reverse);
	}

	public static SortedRenderLists empty() {
		return EMPTY;
	}
}
