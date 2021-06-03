package com.kruskal.resilix.core.window;

import com.kruskal.resilix.core.Configuration;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A {@link TimeBasedWindow} aggregates error rate from the last <i>t</i> ({@link Configuration#getWindowTimeRange()}) milliseconds.
 */
public class TimeBasedWindow extends AbstractSlidingWindow {

  private final Deque<Long> successAttemptWindow = new ConcurrentLinkedDeque<>();
  private final Deque<Long> failureAttemptWindow = new ConcurrentLinkedDeque<>();
  private final AtomicBoolean writeLock = new AtomicBoolean(true);

  public TimeBasedWindow(Configuration configuration) {
    super(configuration);
  }

  @Override
  public void handleAckAttempt(boolean success) {
    long lastAttempt = System.currentTimeMillis();
    if(success){
      successAttemptWindow.addLast(lastAttempt);
    }
    else {
      failureAttemptWindow.addLast(lastAttempt);
    }
    this.examineAttemptWindow();
  }

  @Override
  protected int getQueSize() {
    this.examineAttemptWindow();
    return successAttemptWindow.size() + failureAttemptWindow.size();
  }

  @Override
  protected double getErrorRateAfterMinCallSatisfied() {
    if(failureAttemptWindow.isEmpty() && successAttemptWindow.isEmpty()) return 0.0d;
    return ((double) failureAttemptWindow.size()) / (failureAttemptWindow.size() + successAttemptWindow.size());
  }

  private void examineAttemptWindow(){

    if(!writeLock.getAndSet(false)) return;
    try{
        while(!successAttemptWindow.isEmpty() &&
            successAttemptWindow.getFirst() < System.currentTimeMillis() - configuration.getWindowTimeRange()){
          successAttemptWindow.removeFirst();
        }
        while(!failureAttemptWindow.isEmpty() &&
            failureAttemptWindow.getFirst() < System.currentTimeMillis() - configuration.getWindowTimeRange()){
          failureAttemptWindow.removeFirst();
        }
      }
    catch (Throwable t){}
    finally {
      writeLock.set(true);
    }
  }

  @Override
  public void clear() {
    failureAttemptWindow.clear();
    successAttemptWindow.clear();
  }
}
