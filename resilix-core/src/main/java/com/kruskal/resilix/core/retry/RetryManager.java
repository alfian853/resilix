package com.kruskal.resilix.core.retry;


import com.kruskal.resilix.core.window.SlidingWindowObserver;

public interface RetryManager extends SlidingWindowObserver {
  boolean acquireAndUpdateRetryPermission();
  RetryState getRetryState();
  double getErrorRate();
}
