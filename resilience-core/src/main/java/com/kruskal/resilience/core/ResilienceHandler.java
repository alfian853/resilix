package com.kruskal.resilience.core;

import com.kruskal.resilience.core.state.CloseStateHandler;
import com.kruskal.resilience.core.state.StateHandler;

public class ResilienceHandler implements ExecutionHandler, StateContainer {

  private StateHandler stateStrategy;

  public ResilienceHandler(Context context){
    stateStrategy = new CloseStateHandler(context, this);
  }

  @Override
  public boolean acquirePermission() {
    return stateStrategy.acquirePermission();
  }

  @Override
  public boolean execute(Runnable runnable) {
    return stateStrategy.execute(runnable);
  }

  @Override
  public void setStateStrategy(StateHandler newStateStrategy) {
    stateStrategy = newStateStrategy;
  }

  @Override
  public StateHandler getStateStrategy() {
    return stateStrategy;
  }

}
