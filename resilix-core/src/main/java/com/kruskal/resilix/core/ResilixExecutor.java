package com.kruskal.resilix.core;

import java.util.function.Supplier;

public interface ResilixExecutor extends CheckedExecutor {
  boolean execute(Runnable runnable);
  <T> ResultWrapper<T> execute(Supplier<T> supplier);
}
