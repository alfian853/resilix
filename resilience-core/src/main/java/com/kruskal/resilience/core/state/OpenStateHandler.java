package com.kruskal.resilience.core.state;

import com.kruskal.resilience.core.Context;
import com.kruskal.resilience.core.StateContainer;

public class OpenStateHandler extends AbstractStateHandler {

  private final long startingTime;

  public OpenStateHandler(Context context, StateContainer stateManager) {
    super(context, stateManager);

    this.startingTime = System.currentTimeMillis();
  }

  @Override
  public boolean acquirePermission() {
    this.evaluateState();

    if(stateManager.getStateStrategy() != this){
      return stateManager.getStateStrategy().acquirePermission();
    }

    return false;
  }

  @Override
  public void evaluateState() {
    if((startingTime + configuration.getRetryWaitDuration()) >= System.currentTimeMillis()){
      stateManager.setStateStrategy(new HalfOpenStateHandler(context, stateManager));
    }
  }


}
