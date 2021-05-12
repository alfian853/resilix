package com.kruskal.resilix.core.retry;

import com.kruskal.resilix.core.Context;

import java.util.concurrent.atomic.AtomicBoolean;

public class PessimisticRetryManager extends OptimisticRetryManager {

  private final AtomicBoolean isAvailable = new AtomicBoolean(true);

  public PessimisticRetryManager(Context context) {
    super(context);
  }

  @Override
  public boolean acquireAndUpdateRetryPermission() {
    return isAvailable.getAndSet(false) && super.acquireAndUpdateRetryPermission();
  }

  @Override
  public void notifyOnAckAttempt(boolean success) {
    super.notifyOnAckAttempt(success);
    isAvailable.set(true);
  }
}
