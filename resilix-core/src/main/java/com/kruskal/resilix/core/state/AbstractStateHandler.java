package com.kruskal.resilix.core.state;

import com.kruskal.resilix.core.Configuration;
import com.kruskal.resilix.core.Context;
import com.kruskal.resilix.core.ExecutionDeniedException;
import com.kruskal.resilix.core.StateContainer;
import com.kruskal.resilix.core.window.SlidingWindow;

import java.util.function.Supplier;

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
  public void execute(Runnable runnable) throws ExecutionDeniedException {
    if(!this.acquirePermission()) throw new ExecutionDeniedException();

    boolean success = true;
    try {
      this.onBeforeExecution();
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
  public <T> T execute(Supplier<T> supplier) throws ExecutionDeniedException {
    if(!this.acquirePermission()) throw new ExecutionDeniedException();
    boolean success = true;
    T result = null;

    try {
      this.onBeforeExecution();
      result = supplier.get();
      return result;
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

  protected void onBeforeExecution() {
    // do nothing
  }

  protected abstract boolean isSlidingWindowActive();

}
