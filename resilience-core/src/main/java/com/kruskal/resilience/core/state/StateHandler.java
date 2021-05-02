package com.kruskal.resilience.core.state;

public interface StateHandler {
  boolean acquirePermission();
  boolean execute(Runnable runnable);
  void evaluateState();
}
