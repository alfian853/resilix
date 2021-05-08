package com.kruskal.resilience.core;

import com.kruskal.resilience.core.state.CloseStateHandler;
import com.kruskal.resilience.core.state.StateHandler;

public class ResilienceHandler implements ResilienceExecutor, StateContainer {

  private StateHandler stateHandler;

  public ResilienceHandler(Context context){
    stateHandler = new CloseStateHandler(context, this);
  }

  @Override
  public boolean acquirePermission() {
    return stateHandler.acquirePermission();
  }

  @Override
  public boolean execute(Runnable runnable) {
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
