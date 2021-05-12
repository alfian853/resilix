package com.kruskal.resilix.core;

public interface ResilixExecutor {
  boolean execute(Runnable runnable);
  <T> ResultWrapper<T> execute(XSupplier<T> supplier);
}
