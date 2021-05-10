package com.kruskal.resilix.core;

import com.kruskal.resilix.core.state.CloseStateHandler;
import com.kruskal.resilix.core.state.StateHandler;

import java.util.function.Supplier;

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
  public void execute(Runnable runnable) throws ExecutionDeniedException {
    stateHandler.evaluateState();
    stateHandler.execute(runnable);
  }

  @Override
  public <T> T execute(Supplier<T> supplier) throws ExecutionDeniedException {
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
