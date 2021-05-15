package com.kruskal.resilix.core.util;

/**
 * It is like {@link Runnable} but might throw an {@link Throwable}.
 */
@FunctionalInterface
public interface CheckedRunnable {
  void run() throws Throwable;
}
