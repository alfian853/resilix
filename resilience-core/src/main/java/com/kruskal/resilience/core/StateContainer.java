package com.kruskal.resilience.core;

import com.kruskal.resilience.core.state.StateHandler;

public interface StateContainer {
  void setStateStrategy(StateHandler newStateStrategy);
  StateHandler getStateStrategy();
}
