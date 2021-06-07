package com.kruskal.resilix.core.state;

import com.kruskal.resilix.core.Context;
import com.kruskal.resilix.core.StateContainer;

public class OpenStateHandler extends AbstractStateHandler {

  private final long endTime;
  private final StateContainer stateContainer;

  public OpenStateHandler(Context context, StateContainer stateContainer) {
    super(context);
    this.stateContainer = stateContainer;
    this.endTime = System.currentTimeMillis() + context.getConfiguration().getWaitDurationInOpenState();
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
    if(System.currentTimeMillis() > endTime){
      stateContainer.setStateHandler(new HalfOpenStateHandler(context, stateContainer));
    }
  }


}
