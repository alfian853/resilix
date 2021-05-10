package com.kruskal.resilix.core;

import com.kruskal.resilix.core.state.StateHandler;

public interface StateContainer {
  void setStateHandler(StateHandler newStateHandler);
  StateHandler getStateHandler();
}
