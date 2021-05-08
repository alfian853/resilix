package com.kruskal.resilience.core.window;

import com.kruskal.resilience.core.Configuration;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CountBasedWindow extends AbstractSlidingWindow {

  private final AtomicInteger errorCount = new AtomicInteger();
  private final Deque<Boolean> windowQue = new ConcurrentLinkedDeque<>();
  private final AtomicBoolean updateQueLock = new AtomicBoolean(true);

  public CountBasedWindow(Configuration configuration) {
    super(configuration);
  }

  @Override
  public void handleAckAttempt(boolean success) {
    windowQue.addLast(success);
    if(!success) errorCount.incrementAndGet();
    this.examineAttemptWindow();
  }

  @Override
  protected int getQueSize() {
    return windowQue.size();
  }

  @Override
  protected double getErrorRateAfterMinCallSatisfied() {
    if(windowQue.isEmpty()) return 0.0d;
    return ((double) errorCount.get()) / windowQue.size();
  }

  @Override
  public void clear() {
    windowQue.clear();
    errorCount.set(0);
  }

  private void examineAttemptWindow(){
    if(updateQueLock.compareAndSet(true, false)){
      while (windowQue.size() > configuration.getSlidingWindowMaxSize()){
        if(Boolean.TRUE.equals(windowQue.removeFirst())){
          // do nothing
        }
        else {
          errorCount.decrementAndGet();
        }
      }
      updateQueLock.set(true);
    }
  }

}
