package com.kruskal.resilix.core.state;

import com.kruskal.resilix.core.Context;
import com.kruskal.resilix.core.StateContainer;

public class OpenStateHandler extends AbstractStateHandler {

  private final long startingTime;

  public OpenStateHandler(Context context, StateContainer stateManager) {
    super(context, stateManager);

    this.startingTime = System.currentTimeMillis();
  }

  @Override
  protected boolean isSlidingWindowActive() {
    return false;
  }

  @Override
  public boolean acquirePermission() {
    this.evaluateState();

    if(stateContainer.getStateHandler() != this){
      return stateContainer.getStateHandler().acquirePermission();
    }

    return false;
  }

  /**
   * Move to {@link HalfOpenStateHandler} if the waiting time is over.
   */
  @Override
  public void evaluateState() {
    if(System.currentTimeMillis() >= (startingTime + configuration.getWaitDurationInOpenState())){
      stateContainer.setStateHandler(new HalfOpenStateHandler(context, stateContainer));
    }
  }


}
