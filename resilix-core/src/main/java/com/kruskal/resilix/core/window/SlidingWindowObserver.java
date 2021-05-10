package com.kruskal.resilix.core.window;

public interface SlidingWindowObserver {
  void notifyOnAckAttempt(boolean success);
}
