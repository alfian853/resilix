package com.kruskal.resilience.core;

public interface ResilienceService {
  boolean acquirePermission();
  boolean execute(Runnable runnable);
}
