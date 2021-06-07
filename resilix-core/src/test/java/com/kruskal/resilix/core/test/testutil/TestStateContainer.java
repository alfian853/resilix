package com.kruskal.resilix.core.test.testutil;

import com.kruskal.resilix.core.StateContainer;
import com.kruskal.resilix.core.state.StateHandler;

public class TestStateContainer implements StateContainer {

  private StateHandler stateHandler;

  @Override
  public void setStateHandler(StateHandler newStateHandler) {
    this.stateHandler = newStateHandler;
  }

  @Override
  public StateHandler getStateHandler() {
    stateHandler.evaluateState();
    return stateHandler;
  }
}
