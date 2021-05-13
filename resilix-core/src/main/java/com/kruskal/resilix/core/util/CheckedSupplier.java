package com.kruskal.resilix.core.util;

@FunctionalInterface
public interface CheckedSupplier<T> {
  T get() throws Throwable;
}
