package com.kruskal.resilix.core.executor;

import com.kruskal.resilix.core.ExecResult;
import com.kruskal.resilix.core.util.CheckedRunnable;
import com.kruskal.resilix.core.util.CheckedSupplier;

public abstract class DefaultCheckedExecutor implements CheckedExecutor {

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
  public <T> ExecResult<T> executeChecked(CheckedSupplier<T> checkedSupplier) throws Throwable {
    if(!this.acquirePermission()) return ExecResult.notExecutedResult();
    boolean success = true;

    try {
      return ExecResult.executionResult(checkedSupplier.get());
    }
    catch (Throwable throwable){
      success = false;
      throw throwable;
    } finally {
      this.onAfterExecution(success);
    }

  }
  /**
   * Try to obtain permission to execute a call.
   * @return true if permitted <br>
   *         false if not permitted
   */
  protected abstract boolean acquirePermission();

  protected abstract void onAfterExecution(boolean success);

}
