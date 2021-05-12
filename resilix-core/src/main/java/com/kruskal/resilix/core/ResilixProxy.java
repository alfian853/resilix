package com.kruskal.resilix.core;

import com.kruskal.resilix.core.state.CloseStateHandler;
import com.kruskal.resilix.core.state.StateHandler;

public class ResilixProxy implements ResilixService, StateContainer {

  private StateHandler stateHandler;

  public ResilixProxy(Context context){
    stateHandler = new CloseStateHandler(context, this);
  }

  @Override
  public boolean checkPermission() {
    stateHandler.evaluateState();
    return stateHandler.checkPermission();
  }

  @Override
  public void execute(Runnable runnable) throws ExecutionDeniedException {
    stateHandler.evaluateState();
    stateHandler.execute(runnable);
  }

  @Override
  public <T> T execute(XSupplier<T> supplier) throws ExecutionDeniedException {
    stateHandler.evaluateState();
    return stateHandler.execute(supplier);
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
