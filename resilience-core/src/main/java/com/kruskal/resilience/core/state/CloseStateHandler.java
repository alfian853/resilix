package com.kruskal.resilience.core.state;

import com.kruskal.resilience.core.Context;
import com.kruskal.resilience.core.StateContainer;

public class CloseStateHandler extends AbstractStateHandler {

  public CloseStateHandler(Context context, StateContainer stateManager) {
    super(context, stateManager);
    context.getSlidingWindow().clear();
  }

  @Override
  protected boolean isSlidingWindowActive() {
    return true;
  }

  @Override
  public boolean acquirePermission() {
    return slidingWindow.getErrorRate() < context.getConfiguration().getErrorThreshold();
  }

  @Override
  public void evaluateState() {
    if(!this.acquirePermission()){
      stateContainer.setStateHandler(new OpenStateHandler(context, stateContainer));
    }
  }

}
