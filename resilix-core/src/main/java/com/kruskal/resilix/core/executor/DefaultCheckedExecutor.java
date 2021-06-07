package com.kruskal.resilix.core.executor;

import com.kruskal.resilix.core.*;
import com.kruskal.resilix.core.executor.CheckedExecutor;
import com.kruskal.resilix.core.util.CheckedRunnable;
import com.kruskal.resilix.core.util.CheckedSupplier;

public abstract class DefaultCheckedExecutor implements CheckedExecutor {

  protected Context context;
  protected Configuration configuration;

  protected DefaultCheckedExecutor(Context context) {
    this.context = context;

    this.configuration = context.getConfiguration();
  }

  @Override
  public boolean executeChecked(CheckedRunnable checkedRunnable) throws Throwable {
    if(!this.acquirePermission()) return false;

    boolean success = true;
    try {
      checkedRunnable.run();
      return true;
    }
    catch (Throwable throwable){
      success = false;
      throw throwable;
    }
    finally {
      this.onAfterExecution(success);
    }
  }

  @Override
  public <T> ResultWrapper<T> executeChecked(CheckedSupplier<T> checkedSupplier) throws Throwable {
    if(!this.acquirePermission()) return ResultWrapper.notExecutedResult();
    boolean success = true;

    try {
      return ResultWrapper.executionResult(checkedSupplier.get());
    }
    catch (Throwable throwable){
      success = false;
      throw throwable;
    } finally {
      this.onAfterExecution(success);
    }

  }

  protected abstract boolean acquirePermission();

  protected abstract void onAfterExecution(boolean success);

}
