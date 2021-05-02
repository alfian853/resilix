package com.kruskal.resilience.core.window;

import com.kruskal.resilience.core.Configuration;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public abstract class AbstractSlidingWindow implements SlidingWindow {

  protected Configuration configuration;

  private final Queue<SlidingWindowObserver> slidingWindowSubscribers =
      new ConcurrentLinkedDeque<>();

  protected AbstractSlidingWindow(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public void addObserver(SlidingWindowObserver slidingWindowObserver) {
    this.slidingWindowSubscribers.add(slidingWindowObserver);
  }

  @Override
  public void removeObserver(SlidingWindowObserver slidingWindowObserver) {
    this.slidingWindowSubscribers.remove(slidingWindowObserver);
  }

  @Override
  public void ackAttempt(boolean success) {
    this.handleAckAttempt(success);
    this.slidingWindowSubscribers.forEach(subscriber -> subscriber.notifyOnAckAttempt(success));
  }

  protected abstract void handleAckAttempt(boolean success);
}
