package com.kruskal.resilix.core;

@FunctionalInterface
public interface XSupplier<T> {
  T get() throws Exception;
}
