package com.kruskal.resilix.core;

import com.kruskal.resilix.core.state.CloseStateHandler;
import com.kruskal.resilix.core.state.StateHandler;

public class ResilixProxy implements ResilixService, StateContainer {

  private StateHandler stateHandler;

  public ResilixProxy(Context context){
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
