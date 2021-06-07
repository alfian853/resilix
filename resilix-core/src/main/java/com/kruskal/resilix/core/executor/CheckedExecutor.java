package com.kruskal.resilix.core.executor;

import com.kruskal.resilix.core.ExecResult;
import com.kruskal.resilix.core.util.CheckedRunnable;
import com.kruskal.resilix.core.util.CheckedSupplier;

/**
 * A {@link CheckedExecutor} handles execution calls for functional interfaces that might have
 * a <b>throws</b> method signature.
 */
public interface CheckedExecutor {

  /**
   * @param checkedRunnable Please read {@link CheckedRunnable}
   * @return true if the execution is finished without any error occurred <br>
   *         false if the execution is denied
   * @throws Throwable that is thrown by CheckedRunnable
   */
  boolean executeChecked(CheckedRunnable checkedRunnable) throws Throwable;


  /**
   * @param checkedSupplier See {@link CheckedSupplier}
   * @param <T> The return type
   * @return {@link ExecResult} contains every information about the execution
   * @throws Throwable that is thrown by CheckedSupplier
   */
  <T> ExecResult<T> executeChecked(CheckedSupplier<T> checkedSupplier) throws Throwable;

}
