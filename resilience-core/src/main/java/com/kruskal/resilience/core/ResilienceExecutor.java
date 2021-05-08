package com.kruskal.resilience.core;

public interface ResilienceExecutor {
  boolean acquirePermission();
  boolean execute(Runnable runnable);
}
