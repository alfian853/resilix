package com.kruskal.resilix.core;

import java.util.function.Function;

public interface ResilixExecutor {
  boolean acquirePermission();
  boolean execute(Runnable runnable);
  <T,R> T execute(Function<T, R> function);

}
