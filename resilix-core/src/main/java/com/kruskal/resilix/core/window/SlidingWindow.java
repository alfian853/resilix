package com.kruskal.resilix.core.window;

public interface SlidingWindow {
  void ackAttempt(boolean success);
  double getErrorRate();
  void setActive(boolean isActive);
  void clear();
  void addObserver(SlidingWindowObserver slidingWindowObserver);
  void removeObserver(SlidingWindowObserver slidingWindowObserver);
}
