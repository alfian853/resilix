package com.kruskal.resilix.core.util;

@FunctionalInterface
public interface CheckedRunnable {
  void run() throws Throwable;
}
