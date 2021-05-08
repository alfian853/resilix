package com.kruskal.resilience.core;

import com.kruskal.resilience.core.state.CloseStateHandler;
import com.kruskal.resilience.core.state.StateHandler;

public class ResilienceProxy implements ResilienceService, StateContainer {

  private StateHandler stateHandler;

  public ResilienceProxy(Context context){
    stateHandler = new CloseStateHandler(context, this);
  }

  @Override
  public boolean acquirePermission() {
    stateHandler.evaluateState();
    return stateHandler.acquirePermission();
  }

  @Override
  public boolean execute(Runnable runnable) {
    stateHandler.evaluateState();
    return stateHandler.execute(runnable);
  }

  @Override
  public void setStateHandler(StateHandler newStateHandler) {
    stateHandler = newStateHandler;
  }

  @Override
  public StateHandler getStateHandler() {
    stateHandler.evaluateState();
    return stateHandler;
  }

}
