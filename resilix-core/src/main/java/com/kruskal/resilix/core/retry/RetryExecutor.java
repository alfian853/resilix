package com.kruskal.resilix.core.retry;


import com.kruskal.resilix.core.executor.CheckedExecutor;

/**
 * A {@link RetryExecutor} handles permission for retry process.
 */
public interface RetryExecutor extends CheckedExecutor {
    /**
   * Return current state of retry process. Please read {@link RetryState} for more information.
   * @return {@link RetryState}
   */
  RetryState getRetryState();

}
