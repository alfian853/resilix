package com.kruskal.resilix.core;

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
   *         false if the execution is denied due to its state
   * @throws Throwable that is thrown by CheckedRunnable
   */
  boolean executeChecked(CheckedRunnable checkedRunnable) throws Throwable;


  /**
   * @param checkedSupplier See {@link CheckedSupplier}
   * @param <T> The return type
   * @return {@link ResultWrapper} that contain every information about the execution
   * @throws Throwable that is thrown by CheckedSupplier
   */
  <T> ResultWrapper<T> executeChecked(CheckedSupplier<T> checkedSupplier) throws Throwable;

}
