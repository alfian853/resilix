package com.kruskal.resilix.core.retry;


import com.kruskal.resilix.core.window.SlidingWindowObserver;

/**
 * A {@link RetryManager} handles permission for retry process.
 */
public interface RetryManager extends SlidingWindowObserver {
  /**
   * Get permission and update counter for call attempt or others data.
   * @return true if is permitted <br>
   *         false if is not permitted
   */
  boolean acquireAndUpdateRetryPermission();

  /**
   * Return current state of retry process. Please read {@link RetryState} for more information.
   * @return {@link RetryState}
   */
  RetryState getRetryState();

  double getErrorRate();
}
