package com.kruskal.resilience.core.window;

public interface SlidingWindow {
  void ackAttempt(boolean success);
  double getErrorRate();
  void setActive(boolean isActive);
  void addObserver(SlidingWindowObserver slidingWindowObserver);
  void removeObserver(SlidingWindowObserver slidingWindowObserver);
}
