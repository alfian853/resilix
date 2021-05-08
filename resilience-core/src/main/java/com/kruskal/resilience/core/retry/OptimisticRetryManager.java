package com.kruskal.resilience.core.retry;

import com.kruskal.resilience.core.Configuration;
import com.kruskal.resilience.core.Context;
import com.kruskal.resilience.core.window.SlidingWindowObserver;

import java.util.concurrent.atomic.AtomicInteger;

public class OptimisticRetryManager implements RetryManager, SlidingWindowObserver {

  private final AtomicInteger numberOfRetry = new AtomicInteger(0);
  private final AtomicInteger numberOfFail = new AtomicInteger(0);
  private final Context context;
  private final Configuration configuration;

  public OptimisticRetryManager(Context context) {
    this.context = context;
    this.configuration = context.getConfiguration();

    context.getSlidingWindow().addObserver(this);
  }

  @Override
  public boolean acquireRetryPermission() {

    if(numberOfRetry.get() >= configuration.getNumberOfRetryInHalfOpenState()){
      return false;
    }

    return !this.isErrorLimitExceeded();
  }

  @Override
  public RetryState getRetryState() {
    if(this.isErrorLimitExceeded()){
      context.getSlidingWindow().removeObserver(this);
      return RetryState.REJECTED;
    }

    if(numberOfRetry.get() >= configuration.getNumberOfRetryInHalfOpenState()){
      context.getSlidingWindow().removeObserver(this);
      return RetryState.ACCEPTED;
    }

    return RetryState.ON_GOING;
  }

  @Override
  public void onBeforeRetry() {

  }

  @Override
  public double getErrorRate() {
    if(numberOfRetry.get() == 0) return 0.0d;
    return ((double) numberOfFail.get()) / numberOfRetry.get();
  }

  @Override
  public void notifyOnAckAttempt(boolean success) {
    numberOfRetry.incrementAndGet();

    if(!success) numberOfFail.incrementAndGet();
  }

  private boolean isErrorLimitExceeded(){
    return this.getErrorRate() > configuration.getErrorThreshold();
  }

}
