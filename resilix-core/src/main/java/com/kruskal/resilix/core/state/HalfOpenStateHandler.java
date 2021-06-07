package com.kruskal.resilix.core.state;

import com.kruskal.resilix.core.Context;
import com.kruskal.resilix.core.ExecResult;
import com.kruskal.resilix.core.StateContainer;
import com.kruskal.resilix.core.factory.RetryExecutorFactory;
import com.kruskal.resilix.core.retry.RetryExecutor;
import com.kruskal.resilix.core.util.CheckedRunnable;
import com.kruskal.resilix.core.util.CheckedSupplier;

public class HalfOpenStateHandler extends AbstractStateHandler {

  private final RetryExecutor retryExecutor;
  private final StateContainer stateContainer;

  public HalfOpenStateHandler(Context context, StateContainer stateContainer) {
    super(context);
    this.stateContainer = stateContainer;
    this.retryExecutor = RetryExecutorFactory.create(context);
  }

  @Override
  public boolean executeChecked(CheckedRunnable checkedRunnable) throws Throwable {
    return retryExecutor.executeChecked(checkedRunnable);
  }

  @Override
  public <T> ExecResult<T> executeChecked(CheckedSupplier<T> checkedSupplier) throws Throwable {
    return retryExecutor.executeChecked(checkedSupplier);
  }

  @Override
  public boolean acquirePermission() {
    return false;
  }

  @Override
  public void evaluateState() {

    switch (retryExecutor.getRetryState()){
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
