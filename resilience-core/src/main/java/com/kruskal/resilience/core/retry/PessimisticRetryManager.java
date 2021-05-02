package com.kruskal.resilience.core.retry;

import com.kruskal.resilience.core.Context;

import java.util.concurrent.atomic.AtomicBoolean;

public class PessimisticRetryManager extends OptimisticRetryManager {

  private final AtomicBoolean isAvailable = new AtomicBoolean(true);

  public PessimisticRetryManager(Context context) {
    super(context);
  }

  @Override
  public boolean acquireRetryPermission() {
    return isAvailable.get() && super.acquireRetryPermission();
  }

  @Override
  public void onBeforeRetry() {
    isAvailable.set(false);
  }

  @Override
  public void notifyOnAckAttempt(boolean success) {
    super.notifyOnAckAttempt(success);
    isAvailable.set(true);
  }
}
