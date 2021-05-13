package com.kruskal.resilix.core.state;

import com.kruskal.resilix.core.CheckedExecutor;

public interface StateHandler extends CheckedExecutor {
  boolean acquirePermission();
  void evaluateState();
}
