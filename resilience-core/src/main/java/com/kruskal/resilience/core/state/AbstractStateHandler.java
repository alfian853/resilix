package com.kruskal.resilience.core.state;

import com.kruskal.resilience.core.Configuration;
import com.kruskal.resilience.core.Context;
import com.kruskal.resilience.core.StateContainer;
import com.kruskal.resilience.core.window.SlidingWindow;

public abstract class AbstractStateHandler implements StateHandler {

  protected Context context;
  protected Configuration configuration;
  protected SlidingWindow slidingWindow;
  protected StateContainer stateContainer;

  protected AbstractStateHandler(Context context, StateContainer stateContainer) {
    this.context = context;
    this.stateContainer = stateContainer;

    this.configuration = context.getConfiguration();
    this.slidingWindow = context.getSlidingWindow();
    this.slidingWindow.setActive(this.isSlidingWindowActive());
  }

  @Override
  public boolean execute(Runnable runnable) {
    if(!this.acquirePermission()) return false;
    boolean success = true;
    try {
      this.onBeforeExecution();
      runnable.run();
    }
    catch (Exception e){
      success = false;
    }
    slidingWindow.ackAttempt(success);
    this.evaluateState();

    return success;
  }

  protected void onBeforeExecution() {
    // do nothing
  }

  abstract protected boolean isSlidingWindowActive();

}
