package com.kruskal.resilix.core.state;

import com.kruskal.resilix.core.ResilixExecutor;

public interface StateHandler extends ResilixExecutor {
  boolean acquirePermission();
  void evaluateState();
}
