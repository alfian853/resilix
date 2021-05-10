package com.kruskal.resilix.core;

public interface ResilixService {
  boolean acquirePermission();
  void execute(Runnable runnable) throws ExecutionDeniedException;
  <T> T execute(XSupplier<T> supplier) throws ExecutionDeniedException;
}
