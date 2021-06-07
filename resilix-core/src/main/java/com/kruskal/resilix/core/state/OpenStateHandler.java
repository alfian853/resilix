package com.kruskal.resilix.core.state;

import com.kruskal.resilix.core.Context;
import com.kruskal.resilix.core.StateContainer;

public class OpenStateHandler extends AbstractStateHandler {

  private final long startingTime;
  private final StateContainer stateContainer;

  public OpenStateHandler(Context context, StateContainer stateContainer) {
    super(context);
    this.stateContainer = stateContainer;
    this.startingTime = System.currentTimeMillis();
  }

  @Override
  public boolean acquirePermission() {
    this.evaluateState();
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
