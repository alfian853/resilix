package com.kruskal.resilix.core.retry;

import com.kruskal.resilix.core.Configuration;
import com.kruskal.resilix.core.Context;
import com.kruskal.resilix.core.window.SlidingWindowObserver;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A {@link OptimisticRetryManager} gives the permission as long as the error threshold hasn't reached,
 * and the retry limit ({@link Configuration#getNumberOfRetryInHalfOpenState()}) hasn't exceeded.
 */
public class OptimisticRetryManager implements RetryManager, SlidingWindowObserver {

  private final AtomicInteger numberOfRetry = new AtomicInteger(0);
  private final AtomicInteger numberOfFail = new AtomicInteger(0);
  private AtomicReference<RetryState> retryState = new AtomicReference<>(null);
  protected final Context context;
  private final Configuration configuration;

  public OptimisticRetryManager(Context context) {
    this.context = context;
    this.configuration = context.getConfiguration();

    context.getSlidingWindow().addObserver(this);
  }

  @Override
  public boolean acquireAndUpdateRetryPermission() {

    if(numberOfRetry.getAndIncrement() >= configuration.getNumberOfRetryInHalfOpenState()){
      return false;
    }

    return !this.isErrorLimitExceeded();
  }

  @Override
  public RetryState getRetryState() {

    if(retryState.get() == RetryState.ACCEPTED || retryState.get() == RetryState.REJECTED) {
      return retryState.get();
    }

    if(this.isErrorLimitExceeded()){
      retryState = new AtomicReference<>(RetryState.REJECTED);
      context.getSlidingWindow().removeObserver(this);
      return RetryState.REJECTED;
    }

    if(numberOfRetry.get() >= configuration.getNumberOfRetryInHalfOpenState()){
      retryState = new AtomicReference<>(RetryState.ACCEPTED);
      context.getSlidingWindow().removeObserver(this);
      return RetryState.ACCEPTED;
    }

    return RetryState.ON_GOING;
  }

  @Override
  public double getErrorRate() {
    if(numberOfRetry.get() == 0) return 0.0d;
    return ((double) numberOfFail.get()) / numberOfRetry.get();
  }

  @Override
  public void notifyOnAckAttempt(boolean success) {

    if(!success) numberOfFail.incrementAndGet();
  }

  private boolean isErrorLimitExceeded(){
    if(retryState.get() == RetryState.REJECTED) return true;
    return this.getErrorRate() >= configuration.getErrorThreshold();
  }

}
