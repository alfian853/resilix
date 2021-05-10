package com.kruskal.resilix.core;

import java.util.function.Supplier;

public interface ResilixService {
  boolean acquirePermission();
  void execute(Runnable runnable) throws ExecutionDeniedException;
  <T> T execute(Supplier<T> supplier) throws ExecutionDeniedException;
}
