package com.kruskal.resilix.core.window;

/**
 * {@link SlidingWindowObserver} is an observer for {@link SlidingWindow}
 */
public interface SlidingWindowObserver {
  void notifyOnAckAttempt(boolean success);
}
