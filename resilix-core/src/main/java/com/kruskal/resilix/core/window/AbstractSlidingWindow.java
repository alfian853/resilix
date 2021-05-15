package com.kruskal.resilix.core.window;

import com.kruskal.resilix.core.Configuration;

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

  /**
   * @return The number of current ack.
   */
  protected abstract int getQueSize();

  /**
   * @return 0 if {@link Configuration#minimumCallToEvaluate} hasn't reached yet
   *         and else count the actual error rate.
   */
  protected abstract double getErrorRateAfterMinCallSatisfied();

}
