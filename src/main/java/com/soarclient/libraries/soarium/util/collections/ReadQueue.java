package com.soarclient.libraries.soarium.util.collections;

import org.jetbrains.annotations.Nullable;

public interface ReadQueue<E> {
	@Nullable
	E dequeue();
}
