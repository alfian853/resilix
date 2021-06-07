package com.kruskal.resilix.core.state;

import com.kruskal.resilix.core.Context;
import com.kruskal.resilix.core.StateContainer;

public class CloseStateHandler extends AbstractStateHandler {

  private final StateContainer stateContainer;

  public CloseStateHandler(Context context, StateContainer stateContainer) {
    super(context);
    this.stateContainer = stateContainer;
    context.getSlidingWindow().clear();
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
