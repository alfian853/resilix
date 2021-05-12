package com.kruskal.resilix.core.state;

import com.kruskal.resilix.core.*;
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
  public void execute(Runnable runnable) throws ExecutionDeniedException {
    if(!this.checkPermission()) throw new ExecutionDeniedException();

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
    if(!this.checkPermission()) throw new ExecutionDeniedException();
    boolean success = true;
    T result = null;

    try {
      result = supplier.get();
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

  protected abstract boolean isSlidingWindowActive();

}
