package com.kruskal.resilix.core;

import com.kruskal.resilix.core.retry.RetryStrategy;
import com.kruskal.resilix.core.window.SlidingWindowStrategy;

public class Configuration {

  private static final int SECOND_IN_MS = 1000;

  private SlidingWindowStrategy slidingWindowStrategy = SlidingWindowStrategy.COUNT_BASED;
  private RetryStrategy retryStrategy = RetryStrategy.OPTIMISTIC;
  private long slidingWindowTimeRange = 10L * SECOND_IN_MS;
  private int slidingWindowMaxSize = 20;
  private int minimumCallToEvaluate = 5;
  private double errorThreshold = 0.5d;
  private long waitDurationInOpenState = 5L;
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

  public void setSlidingWindowMaxSize(int slidingWindowMaxSize) {
    this.slidingWindowMaxSize = slidingWindowMaxSize;
  }

  public double getErrorThreshold() {
    return errorThreshold;
  }

  public void setErrorThreshold(double errorThreshold) {
    this.errorThreshold = errorThreshold;
  }

  public long getWaitDurationInOpenState() {
    return waitDurationInOpenState;
  }

  public void setWaitDurationInOpenState(long waitDurationInOpenState) {
    this.waitDurationInOpenState = waitDurationInOpenState;
  }

  public int getNumberOfRetryInHalfOpenState() {
    return numberOfRetryInHalfOpenState;
  }

  public void setNumberOfRetryInHalfOpenState(int numberOfRetryInHalfOpenState) {
    this.numberOfRetryInHalfOpenState = numberOfRetryInHalfOpenState;
  }

  public int getMinimumCallToEvaluate() {
    return minimumCallToEvaluate;
  }

  public void setMinimumCallToEvaluate(int minimumCallToEvaluate) {
    this.minimumCallToEvaluate = minimumCallToEvaluate;
  }
}
