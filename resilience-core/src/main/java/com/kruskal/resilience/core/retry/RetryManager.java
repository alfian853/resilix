package com.kruskal.resilience.core.retry;


import com.kruskal.resilience.core.constant.RetryState;
import com.kruskal.resilience.core.window.SlidingWindowObserver;

public interface RetryManager extends SlidingWindowObserver {
  boolean acquireRetryPermission();
  RetryState getRetryState();
  void onBeforeRetry();
  double getErrorRate();
}
