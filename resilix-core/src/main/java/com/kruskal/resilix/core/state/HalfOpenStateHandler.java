package com.kruskal.resilix.core.state;

import com.kruskal.resilix.core.Context;
import com.kruskal.resilix.core.ExecutionDeniedException;
import com.kruskal.resilix.core.StateContainer;
import com.kruskal.resilix.core.XSupplier;
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
  public void execute(Runnable runnable) throws ExecutionDeniedException {
    if(!retryManager.acquireAndUpdateRetryPermission()) throw new ExecutionDeniedException();

    boolean success = true;
    try {
      runnable.run();
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
  public <T> T execute(XSupplier<T> supplier) throws ExecutionDeniedException {
    if(!retryManager.acquireAndUpdateRetryPermission()) throw new ExecutionDeniedException();
    boolean success = true;

    try {
      T result = supplier.get();
      return result;
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
