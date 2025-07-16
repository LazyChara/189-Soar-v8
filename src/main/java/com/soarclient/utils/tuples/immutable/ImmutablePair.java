package com.soarclient.utils.tuples.immutable;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import com.soarclient.utils.tuples.Pair;

public final class ImmutablePair<A, B> extends Pair<A, B> {

	private static final long serialVersionUID = 1L;

	private A a;
	private B b;

	private ImmutablePair(A a, B b) {
		this.a = a;
		this.b = b;
	}

	public static <A, B> ImmutablePair<A, B> of(A a, B b) {
		return new ImmutablePair<>(a, b);
	}

	public Pair<A, A> pairOfFirst() {
		return Pair.of(a);
	}

	public Pair<B, B> pairOfSecond() {
		return Pair.of(b);
	}

	@Override
	public A getFirst() {
		return a;
	}

	@Override
	public B getSecond() {
		return b;
	}

	@Override
	public void setFirst(A a) {
		this.a = a;
	}

	@Override
	public void setSecond(B b) {
		this.b = b;
	}

	@Override
	public <R> R apply(BiFunction<? super A, ? super B, ? extends R> func) {
		return func.apply(a, b);
	}

	@Override
	public void use(BiConsumer<? super A, ? super B> func) {
		func.accept(a, b);
	}
}
