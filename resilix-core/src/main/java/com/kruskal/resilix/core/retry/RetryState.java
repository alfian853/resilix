package com.kruskal.resilix.core.retry;

public enum RetryState {
  /**
   * Still on process and retry limit hasn't exceeded.
   */
  ON_GOING,
  /**
   * Error threshold is reached.
   */
  REJECTED,
  /**
   * Retry limit has been exceeded and the error rate is below the error threshold.
   */
  ACCEPTED;
}
