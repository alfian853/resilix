package com.kruskal.resilix.core.retry;

import com.kruskal.resilix.core.Configuration;
import com.kruskal.resilix.core.Context;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A {@link PessimisticRetryExecutor} gives permission once at a time as long as the error threshold hasn't reached,
 * and the retry limit ({@link Configuration#getNumberOfRetryInHalfOpenState()}) hasn't exceeded.
 */
public class PessimisticRetryExecutor extends OptimisticRetryExecutor {

  private final AtomicBoolean isAvailable = new AtomicBoolean(true);

  public PessimisticRetryExecutor(Context context) {
    super(context);
  }

  @Override
  public boolean acquirePermission() {
    return isAvailable.getAndSet(false) && super.acquirePermission();
  }

  @Override
  public void onAfterExecution(boolean success) {
    super.onAfterExecution(success);
    isAvailable.set(true);
  }
}
