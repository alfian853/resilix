package com.kruskal.resilience.core.test.testutil;

import com.kruskal.resilience.core.StateContainer;
import com.kruskal.resilience.core.state.StateHandler;

public class TestStateContainer implements StateContainer {

  private StateHandler stateHandler;

  @Override
  public void setStateHandler(StateHandler newStateHandler) {
    this.stateHandler = newStateHandler;
  }

  @Override
  public StateHandler getStateHandler() {
    return stateHandler;
  }
}