package com.kruskal.resilix.core.state;

import com.kruskal.resilix.core.Configuration;
import com.kruskal.resilix.core.Context;
import com.kruskal.resilix.core.executor.DefaultCheckedExecutor;
import com.kruskal.resilix.core.window.SlidingWindow;

public abstract class AbstractStateHandler extends DefaultCheckedExecutor implements StateHandler {

  protected Context context;
  protected Configuration configuration;
  protected SlidingWindow slidingWindow;

  protected AbstractStateHandler(Context context) {
    this.context = context;
    this.configuration = context.getConfiguration();
    this.slidingWindow = context.getSlidingWindow();
  }

  @Override
  protected void onAfterExecution(boolean success) {
    slidingWindow.ackAttempt(success);
  }

  /**
   * Try to obtain permission to execute a call. this method might trigger update for certain state.
   * @return true if permitted <br>
   *         false if not permitted
   */
  protected abstract boolean acquirePermission();
}
