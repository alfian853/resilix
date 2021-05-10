package com.kruskal.resilix.core.state;

import com.kruskal.resilix.core.ResilixService;

public interface StateHandler extends ResilixService {
  void evaluateState();
}
