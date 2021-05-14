package com.kruskal.resilix.core;

import com.kruskal.resilix.core.retry.RetryStrategy;
import com.kruskal.resilix.core.window.SlidingWindowStrategy;

public class Configuration {

  private static final int SECOND_IN_MS = 1000;

  private SlidingWindowStrategy slidingWindowStrategy = SlidingWindowStrategy.COUNT_BASED;
  private RetryStrategy retryStrategy = RetryStrategy.PESSIMISTIC;
  private long slidingWindowTimeRange = 10L * SECOND_IN_MS;
  private int slidingWindowMaxSize = 20;
  private int minimumCallToEvaluate = 5;
  private double errorThreshold = 0.5d;
  private long waitDurationInOpenState = 15L *SECOND_IN_MS;
  private int numberOfRetryInHalfOpenState = 10;

  public SlidingWindowStrategy getSlidingWindowStrategy() {
    return slidingWindowStrategy;
  }

  public void setSlidingWindowStrategy(SlidingWindowStrategy slidingWindowStrategy) {
    if(slidingWindowStrategy == null){
      throw new IllegalArgumentException("slidingWindowStrategy should not be null");
    }
    this.slidingWindowStrategy = slidingWindowStrategy;
  }

  public RetryStrategy getRetryStrategy() {
    return retryStrategy;
  }

  public void setRetryStrategy(RetryStrategy retryStrategy) {
    if(retryStrategy == null){
      throw new IllegalArgumentException("retryStrategy should not be null");
    }
    this.retryStrategy = retryStrategy;
  }

  public long getWindowTimeRange() {
    return slidingWindowTimeRange;
  }

  public void setSlidingWindowTimeRange(long slidingWindowTimeRange) {
    if(slidingWindowTimeRange <= 0){
      throw new IllegalArgumentException("slidingWindowTimeRange should be greater than 0");
    }
    this.slidingWindowTimeRange = slidingWindowTimeRange;
  }

  public int getSlidingWindowMaxSize() {
    return slidingWindowMaxSize;
  }

  public void setSlidingWindowMaxSize(int slidingWindowMaxSize) {
    if(slidingWindowMaxSize <= 0){
      throw new IllegalArgumentException("slidingWindowMaxSize should be greater than 0");
    }
    this.slidingWindowMaxSize = slidingWindowMaxSize;
  }

  public double getErrorThreshold() {
    return errorThreshold;
  }

  public void setErrorThreshold(double errorThreshold) {
    if(errorThreshold <= 0 || errorThreshold > 1.0){
      throw new IllegalArgumentException("errorThreshold should be between 0.0 and 1.0");
    }
    this.errorThreshold = errorThreshold;
  }

  public long getWaitDurationInOpenState() {
    return waitDurationInOpenState;
  }

  public void setWaitDurationInOpenState(long waitDurationInOpenState) {
    if(waitDurationInOpenState < 0){
      throw new IllegalArgumentException("waitDurationInOpenState should be greater or equal to 0");
    }
    this.waitDurationInOpenState = waitDurationInOpenState;
  }

  public int getNumberOfRetryInHalfOpenState() {
    return numberOfRetryInHalfOpenState;
  }

  public void setNumberOfRetryInHalfOpenState(int numberOfRetryInHalfOpenState) {
    if(numberOfRetryInHalfOpenState <= 0){
      throw new IllegalArgumentException("numberOfRetryInHalfOpenState should be greater than 0");
    }
    this.numberOfRetryInHalfOpenState = numberOfRetryInHalfOpenState;
  }

  public int getMinimumCallToEvaluate() {
    return minimumCallToEvaluate;
  }

  public void setMinimumCallToEvaluate(int minimumCallToEvaluate) {
    if(minimumCallToEvaluate < 0){
      throw new IllegalArgumentException("minimumCallToEvaluate should be greater than or equal to 0");
    }
    this.minimumCallToEvaluate = minimumCallToEvaluate;
  }
}
