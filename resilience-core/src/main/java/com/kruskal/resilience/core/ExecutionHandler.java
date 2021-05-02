package com.kruskal.resilience.core;

public interface ExecutionHandler {
  boolean acquirePermission();
  boolean execute(Runnable runnable);
}
