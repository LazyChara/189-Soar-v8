package com.soarclient.utils.tuples;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import com.soarclient.utils.tuples.immutable.ImmutablePair;

public abstract class Pair<A, B> implements Serializable {

	private static final long serialVersionUID = 1L;

	public static <A, B> Pair<A, B> of(A a, B b) {
		return ImmutablePair.of(a, b);
	}

	public static <A> Pair<A, A> of(A a) {
		return ImmutablePair.of(a, a);
	}

	public abstract A getFirst();

	public abstract B getSecond();

	public abstract void setFirst(A a);

	public abstract void setSecond(B b);

	public abstract <R> R apply(BiFunction<? super A, ? super B, ? extends R> func);

	public abstract void use(BiConsumer<? super A, ? super B> func);

	@Override
	public int hashCode() {
		return Objects.hash(getFirst(), getSecond());
	}

	@Override
	public boolean equals(Object that) {

		if (this == that) {
			return true;
		}

		if (that instanceof Pair<?, ?>) {
			final Pair<?, ?> other = (Pair<?, ?>) that;
			return Objects.equals(getFirst(), other.getFirst()) && Objects.equals(getSecond(), other.getSecond());
		}

		return false;
	}
}
