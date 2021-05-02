package com.kruskal.resilience.core;

import com.kruskal.resilience.core.constant.RetryStrategy;
import com.kruskal.resilience.core.constant.SlidingWindowStrategy;

public class Configuration {

  private static final int SECOND_IN_MS = 1000;

  private SlidingWindowStrategy slidingWindowStrategy;
  private RetryStrategy retryStrategy = RetryStrategy.OPTIMISTIC;
  private long slidingWindowTimeRange = 10L * SECOND_IN_MS;
  private long slidingWindowMaxSize = 10L;
  private double errorThreshold = 0.5d;
  private long retryWaitDuration = 5L;
  private int numberOfRetryInHalfOpenState = 10;

  public SlidingWindowStrategy getSlidingWindowStrategy() {
    return slidingWindowStrategy;
  }

  public void setSlidingWindowStrategy(SlidingWindowStrategy slidingWindowStrategy) {
    this.slidingWindowStrategy = slidingWindowStrategy;
  }

  public RetryStrategy getRetryStrategy() {
    return retryStrategy;
  }

  public void setRetryStrategy(RetryStrategy retryStrategy) {
    this.retryStrategy = retryStrategy;
  }

  public long getWindowTimeRange() {
    return slidingWindowTimeRange;
  }

  public void setSlidingWindowTimeRange(long slidingWindowTimeRange) {
    this.slidingWindowTimeRange = slidingWindowTimeRange;
  }

  public long getSlidingWindowMaxSize() {
    return slidingWindowMaxSize;
  }

  public void setSlidingWindowMaxSize(long slidingWindowMaxSize) {
    this.slidingWindowMaxSize = slidingWindowMaxSize;
  }

  public double getErrorThreshold() {
    return errorThreshold;
  }

  public void setErrorThreshold(double errorThreshold) {
    this.errorThreshold = errorThreshold;
  }

  public long getRetryWaitDuration() {
    return retryWaitDuration;
  }

  public void setRetryWaitDuration(long retryWaitDuration) {
    this.retryWaitDuration = retryWaitDuration;
  }

  public int getNumberOfRetryInHalfOpenState() {
    return numberOfRetryInHalfOpenState;
  }

  public void setNumberOfRetryInHalfOpenState(int numberOfRetryInHalfOpenState) {
    this.numberOfRetryInHalfOpenState = numberOfRetryInHalfOpenState;
  }
}
