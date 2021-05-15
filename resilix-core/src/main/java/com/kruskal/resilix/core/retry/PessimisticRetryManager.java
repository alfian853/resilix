package com.kruskal.resilix.core.retry;

import com.kruskal.resilix.core.Configuration;
import com.kruskal.resilix.core.Context;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A {@link PessimisticRetryManager} gives permission once at a time as long as the error threshold hasn't reached,
 * and the retry limit ({@link Configuration#getNumberOfRetryInHalfOpenState()}) hasn't exceeded.
 */
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
