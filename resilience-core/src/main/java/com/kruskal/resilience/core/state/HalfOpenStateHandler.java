package com.kruskal.resilience.core.state;

import com.kruskal.resilience.core.Context;
import com.kruskal.resilience.core.StateContainer;
import com.kruskal.resilience.core.retry.RetryManager;
import com.kruskal.resilience.core.factory.RetryManagerFactory;

public class HalfOpenStateHandler extends AbstractStateHandler {

  private final RetryManager retryManager;

  public HalfOpenStateHandler(Context context, StateContainer stateManager) {
    super(context, stateManager);

    this.retryManager = RetryManagerFactory.create(context);
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
  public void evaluateState() {

    switch (retryManager.getRetryState()){
      case ACCEPTED:
        stateManager.setStateStrategy(new CloseStateHandler(context, stateManager));
        break;
      case REJECTED:
        stateManager.setStateStrategy(new OpenStateHandler(context, stateManager));
        break;
    }

  }


}