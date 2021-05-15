package com.kruskal.resilix.core.util;

/**
 * It is like {@link java.util.function.Supplier} but might throw an {@link Throwable}.
 * @param <T> The wanted return type.
 */
@FunctionalInterface
public interface CheckedSupplier<T> {
  T get() throws Throwable;
}
