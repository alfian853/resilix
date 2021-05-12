package com.kruskal.resilix.core.state;

import com.kruskal.resilix.core.*;
import com.kruskal.resilix.core.factory.RetryManagerFactory;
import com.kruskal.resilix.core.retry.RetryManager;

public class HalfOpenStateHandler extends AbstractStateHandler {

  private final RetryManager retryManager;

  public HalfOpenStateHandler(Context context, StateContainer stateManager) {
    super(context, stateManager);

    this.retryManager = RetryManagerFactory.create(context);
    context.getSlidingWindow().clear();
  }

  @Override
  public boolean checkPermission() {
    return retryManager.checkPermission();
  }

  @Override
  public boolean execute(Runnable runnable) {
    if(!retryManager.acquireAndUpdateRetryPermission()) return false;
    boolean success = true;
    try {
      runnable.run();
      return true;
    }
    catch (Exception e){
      success = false;
      throw e;
    }
    finally {
      slidingWindow.ackAttempt(success);
      this.evaluateState();
    }
  }

  @Override
  public <T> ResultWrapper<T> execute(XSupplier<T> supplier) {
    if(!retryManager.acquireAndUpdateRetryPermission()) return ResultWrapper.notExecutedResult();
    boolean success = true;

    try {
      return ResultWrapper.executionResult(supplier.get());
    }
    catch (Exception e){
      success = false;
      throw new RuntimeException(e);
    } finally {
      slidingWindow.ackAttempt(success);
      this.evaluateState();
    }

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
      case ON_GOING:
        // do nothing
        break;
    }

  }


}
