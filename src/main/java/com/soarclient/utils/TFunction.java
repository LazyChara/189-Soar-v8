package com.soarclient.utils;

@FunctionalInterface
public interface TFunction<T, R> {
	R apply(T t) throws Throwable;
}