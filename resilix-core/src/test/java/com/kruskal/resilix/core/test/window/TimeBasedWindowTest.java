package com.kruskal.resilix.core.test.window;

import com.kruskal.resilix.core.Configuration;
import com.kruskal.resilix.core.test.testutil.RandomUtil;
import com.kruskal.resilix.core.window.SlidingWindowObserver;
import com.kruskal.resilix.core.window.TimeBasedWindow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class TimeBasedWindowTest {

  private final long WINDOW_TIME_RANGE = 250L;
  private final Configuration configuration = new Configuration();
  private final TimeBasedWindow timeBasedWindow = new TimeBasedWindow(configuration);
  private final CountDownLatch waiter = new CountDownLatch(1);

  @BeforeEach
  void init(){
    configuration.setSlidingWindowTimeRange(WINDOW_TIME_RANGE);
  }

  @Test
  @DisplayName("testcase: observe 3 ack, and then unobserve and fire 5 ack")
  void observerNotificationTest() throws InterruptedException {
    AtomicInteger count = new AtomicInteger(0);

    SlidingWindowObserver observer = success -> count.incrementAndGet();
    timeBasedWindow.addObserver(observer);

    waiter.await(WINDOW_TIME_RANGE, TimeUnit.MILLISECONDS);

    for(int i = 0; i < 3; i++){
      timeBasedWindow.ackAttempt(RandomUtil.generateRandomBoolean());
    }

    timeBasedWindow.removeObserver(observer);

    double tmpError = timeBasedWindow.getErrorRate();
    for(int i = 0; i < 5; i++){
      timeBasedWindow.ackAttempt(RandomUtil.generateRandomBoolean());
    }

    Assertions.assertEquals(3, count.get());
    Assertions.assertNotEquals(tmpError, timeBasedWindow.getErrorRate());
  }

  @Test
  @DisplayName("testcase: fire with 25 random ack followed by 10(70% success) ack in arbitrary order after 1000ms later")
  void endingTest() throws InterruptedException {

    Assertions.assertEquals(0.0d, timeBasedWindow.getErrorRate(), 0.000001);

    for(int i = 0; i < 25; i++){
      timeBasedWindow.ackAttempt(RandomUtil.generateRandomBoolean());
    }

    waiter.await(WINDOW_TIME_RANGE + 50, TimeUnit.MILLISECONDS);

    int nSuccess = 7;
    int nFailure = 3;

    while(nSuccess > 0 || nFailure > 0){
      if(RandomUtil.generateRandomBoolean() || nFailure == 0){
        nSuccess--;
        timeBasedWindow.ackAttempt(true);
      }
      else {
        nFailure--;
        timeBasedWindow.ackAttempt(false);
      }
    }

    Assertions.assertEquals(0.3, timeBasedWindow.getErrorRate(), 0.000001);

  }

  @Test
  void clearanceTest(){
    for(int i = 0; i < 25; i++){
      timeBasedWindow.ackAttempt(RandomUtil.generateRandomBoolean());
    }

    Assertions.assertNotEquals(0.0d, timeBasedWindow.getErrorRate(), 0.000001);

    timeBasedWindow.clear();
    Assertions.assertEquals(0.0d, timeBasedWindow.getErrorRate(), 0.000001);

    for(int i = 0; i < configuration.getMinimumCallToEvaluate(); i++){
      timeBasedWindow.ackAttempt(false);
    }
    Assertions.assertEquals(1.0d, timeBasedWindow.getErrorRate(), 0.000001);

    timeBasedWindow.clear();

    for(int i = 0; i < configuration.getMinimumCallToEvaluate(); i++){
      timeBasedWindow.ackAttempt(true);
    }
    Assertions.assertEquals(0.0d, timeBasedWindow.getErrorRate(), 0.000001);
  }

  @Test
  void activeFlagTest(){
    timeBasedWindow.setActive(false);

    for(int i = 0; i < configuration.getSlidingWindowMaxSize(); i++){
      timeBasedWindow.ackAttempt(RandomUtil.generateRandomBoolean());
    }

    Assertions.assertEquals(0.0d, timeBasedWindow.getErrorRate(), 0.000001);
  }

}
