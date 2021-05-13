package com.kruskal.resilix.core.state;

import com.kruskal.resilix.core.*;
import com.kruskal.resilix.core.util.CheckedRunnable;
import com.kruskal.resilix.core.util.CheckedSupplier;
import com.kruskal.resilix.core.window.SlidingWindow;

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
  public boolean executeChecked(CheckedRunnable runnable) throws Throwable {
    if(!this.acquirePermission()) return false;

    boolean success = true;
    try {
      runnable.run();
      return true;
    }
    catch (Throwable throwable){
      success = false;
      throw throwable;
    }
    finally {
      slidingWindow.ackAttempt(success);
      this.evaluateState();
    }
  }

  @Override
  public <T> ResultWrapper<T> executeChecked(CheckedSupplier<T> supplier) throws Throwable {
    if(!this.acquirePermission()) return ResultWrapper.notExecutedResult();
    boolean success = true;

    try {
      return ResultWrapper.executionResult(supplier.get());
    }
    catch (Throwable throwable){
      success = false;
      throw throwable;
    } finally {
      slidingWindow.ackAttempt(success);
      this.evaluateState();
    }

  }

  protected abstract boolean isSlidingWindowActive();

}
