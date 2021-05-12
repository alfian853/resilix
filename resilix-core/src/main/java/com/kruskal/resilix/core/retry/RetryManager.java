package com.kruskal.resilix.core.retry;


import com.kruskal.resilix.core.window.SlidingWindowObserver;

public interface RetryManager extends SlidingWindowObserver {
  boolean checkPermission();
  boolean acquireAndUpdateRetryPermission();
  RetryState getRetryState();
  double getErrorRate();
}
