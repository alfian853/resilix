package com.kruskal.resilience.core.window;

public interface SlidingWindowObserver {
  void notifyOnAckAttempt(boolean success);
}
