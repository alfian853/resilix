package com.kruskal.resilix.core.retry;

import com.kruskal.resilix.core.Configuration;
import com.kruskal.resilix.core.Context;
import com.kruskal.resilix.core.executor.DefaultCheckedExecutor;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A {@link OptimisticRetryExecutor} gives the permission as long as the error threshold hasn't reached,
 * and the retry limit ({@link Configuration#getNumberOfRetryInHalfOpenState()}) hasn't exceeded.
 */
public class OptimisticRetryExecutor extends DefaultCheckedExecutor implements RetryExecutor {

  private final AtomicInteger numberOfRetry = new AtomicInteger(0);
  private final AtomicInteger numberOfFail = new AtomicInteger(0);
  private final AtomicInteger numberOfAck = new AtomicInteger(0);
  private final AtomicReference<RetryState> retryState = new AtomicReference<>(RetryState.ON_GOING);
  protected Configuration configuration;


  public OptimisticRetryExecutor(Context context) {
    this.configuration = context.getConfiguration();
  }

  @Override
  public RetryState getRetryState() {

    if(retryState.get() != RetryState.ON_GOING) {
      return retryState.get();
    }

    if(this.isErrorLimitExceeded()){
      retryState.set(RetryState.REJECTED);
      return RetryState.REJECTED;
    }

    if(numberOfAck.get() >= configuration.getNumberOfRetryInHalfOpenState()){
      retryState.set(RetryState.ACCEPTED);
      return RetryState.ACCEPTED;
    }

    return RetryState.ON_GOING;
  }

  public double getErrorRate() {
    if(numberOfAck.get() == 0) return 0.0d;
    return ((double) numberOfFail.get()) / numberOfAck.get();
  }

  private boolean isErrorLimitExceeded(){
    if(retryState.get() == RetryState.REJECTED) return true;
    return this.getErrorRate() >= configuration.getErrorThreshold();
  }

  @Override
  public boolean acquirePermission() {
    if(numberOfRetry.getAndIncrement() >= configuration.getNumberOfRetryInHalfOpenState()){
      return false;
    }

    return !this.isErrorLimitExceeded();
  }

  @Override
  protected void onAfterExecution(boolean success) {
    numberOfAck.incrementAndGet();
    if(!success){
      numberOfFail.incrementAndGet();
    }
  }
}
