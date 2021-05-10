package com.kruskal.resilix.core.state;

public interface StateHandler {
  boolean acquirePermission();
  boolean execute(Runnable runnable);
  void evaluateState();
}
