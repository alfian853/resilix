package com.kruskal.resilience.core.window;

import com.kruskal.resilience.core.Configuration;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractSlidingWindow implements SlidingWindow {

  protected Configuration configuration;
  protected AtomicBoolean isActive = new AtomicBoolean(true);

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
    if(isActive.get()){
      this.handleAckAttempt(success);
      this.slidingWindowSubscribers.forEach(subscriber -> subscriber.notifyOnAckAttempt(success));
    }
  }

  @Override
  public double getErrorRate() {
    if(this.getQueSize() < configuration.getMinimumCallToEvaluate()){
      return 0.0;
    }

    return getErrorRateAfterMinCallSatisfied();
  }

  @Override
  public void setActive(boolean isActive) {
    this.isActive.set(isActive);
  }

  protected abstract void handleAckAttempt(boolean success);

  protected abstract int getQueSize();

  protected abstract double getErrorRateAfterMinCallSatisfied();

}
