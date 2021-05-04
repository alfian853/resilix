package com.kruskal.resilience.core.window;

import com.kruskal.resilience.core.Configuration;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;

public class TimeBasedWindow extends AbstractSlidingWindow {

  private final Deque<Long> successAttemptWindow = new ConcurrentLinkedDeque<>();
  private final Deque<Long> failureAttemptWindow = new ConcurrentLinkedDeque<>();

  private final AtomicLong lastAttempt = new AtomicLong(0);

  public TimeBasedWindow(Configuration configuration) {
    super(configuration);
  }

  @Override
  public void handleAckAttempt(boolean success) {
    lastAttempt.set(System.currentTimeMillis());
    if(success){
      successAttemptWindow.addLast(lastAttempt.get());
    }
    else {
      failureAttemptWindow.addLast(lastAttempt.longValue());
    }
    this.examineAttemptWindow();
  }

  @Override
  public double getErrorRate() {
    return ((double) failureAttemptWindow.size()) / (failureAttemptWindow.size() + successAttemptWindow.size());
  }

  private void examineAttemptWindow(){
    while(!successAttemptWindow.isEmpty() &&
        successAttemptWindow.getFirst() < lastAttempt.get() - configuration.getWindowTimeRange()){
      successAttemptWindow.removeFirst();
    }
    while(!failureAttemptWindow.isEmpty() &&
        failureAttemptWindow.getFirst() < lastAttempt.get() - configuration.getWindowTimeRange()){
      failureAttemptWindow.removeFirst();
    }

  }
}
