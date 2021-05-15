package com.kruskal.resilix.core.window;

/**
 * A {@link SlidingWindow} manages all information about call results (success/error).
 */
public interface SlidingWindow {

  /** Acknowledges the call result.
   * @param success The ack result.
   */
  void ackAttempt(boolean success);

  /**
   * @return error rate
   */
  double getErrorRate();


  /**
   * Changes the status, if it isn't active, it will ignore {@link #ackAttempt(boolean)} call.
   * @param isActive The status.
   */
  void setActive(boolean isActive);


  /**
   * Clears all ack data.
   */
  void clear();

  /**
   * Adds observer for {@link #ackAttempt(boolean)} calls.
   * */
  void addObserver(SlidingWindowObserver slidingWindowObserver);

  /**
   * Removes observer for {@link #ackAttempt(boolean)} calls.
   * */
  void removeObserver(SlidingWindowObserver slidingWindowObserver);
}
