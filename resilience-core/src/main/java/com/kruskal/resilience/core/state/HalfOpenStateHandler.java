package com.kruskal.resilience.core.state;

import com.kruskal.resilience.core.Context;
import com.kruskal.resilience.core.StateContainer;
import com.kruskal.resilience.core.factory.RetryManagerFactory;
import com.kruskal.resilience.core.retry.RetryManager;

public class HalfOpenStateHandler extends AbstractStateHandler {

  private final RetryManager retryManager;

  public HalfOpenStateHandler(Context context, StateContainer stateManager) {
    super(context, stateManager);

    this.retryManager = RetryManagerFactory.create(context);
    context.getSlidingWindow().clear();
  }

  @Override
  public boolean acquirePermission() {
    return retryManager.acquireRetryPermission();
  }

  @Override
  protected void onBeforeExecution() {
    retryManager.onBeforeRetry();
  }

  @Override
  protected boolean isSlidingWindowActive() {
    return true;
  }

  @Override
  public void evaluateState() {

    switch (retryManager.getRetryState()){
      case ACCEPTED:
        stateContainer.setStateHandler(new CloseStateHandler(context, stateContainer));
        break;
      case REJECTED:
        stateContainer.setStateHandler(new OpenStateHandler(context, stateContainer));
        break;
    }

  }


}
