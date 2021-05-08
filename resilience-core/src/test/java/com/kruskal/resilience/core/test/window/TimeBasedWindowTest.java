package com.kruskal.resilience.core.test.window;

import com.kruskal.resilience.core.Configuration;
import com.kruskal.resilience.core.test.testutil.RandomUtil;
import com.kruskal.resilience.core.window.SlidingWindowObserver;
import com.kruskal.resilience.core.window.TimeBasedWindow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

class TimeBasedWindowTest {

  private final long WINDOW_TIME_RANGE = 1000L;
  private final Configuration configuration = new Configuration();
  private final TimeBasedWindow timeBasedWindow = new TimeBasedWindow(configuration);

  @BeforeEach
  void init(){
    configuration.setSlidingWindowTimeRange(WINDOW_TIME_RANGE);
  }

  private boolean getRandomBoolean() {
    Random random = new Random();
    return random.ints(1, 10)
        .findFirst()
        .getAsInt() % 2 == 0;
  }

  @Test
  @DisplayName("testcase: observe 3 ack, and then unobserve and fire 5 ack")
  void observerNotificationTest() throws InterruptedException {
    AtomicInteger count = new AtomicInteger(0);

    SlidingWindowObserver observer = success -> count.incrementAndGet();
    timeBasedWindow.addObserver(observer);

    Thread.sleep(WINDOW_TIME_RANGE);

    for(int i = 0; i < 3; i++){
      timeBasedWindow.ackAttempt(RandomUtil.generateRandomBoolean());
    }

    timeBasedWindow.removeObserver(observer);

    for(int i = 0; i < 5; i++){
      timeBasedWindow.ackAttempt(RandomUtil.generateRandomBoolean());
    }

    Assertions.assertEquals(3, count.get());
  }

  @Test
  @DisplayName("testcase: fire with 25 random ack followed by 10(70% success) ack in arbitrary order after 1000ms later")
  void endingTest() throws InterruptedException {

    for(int i = 0; i < 25; i++){
      timeBasedWindow.ackAttempt(RandomUtil.generateRandomBoolean());
    }

    Thread.sleep(WINDOW_TIME_RANGE + 1);
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

    double errorRate = timeBasedWindow.getErrorRate();

    Assertions.assertEquals(0.3, errorRate, 0.0001);

  }

  @Test
  void clearanceTest(){
    for(int i = 0; i < 25; i++){
      timeBasedWindow.ackAttempt(RandomUtil.generateRandomBoolean());
    }

    Assertions.assertNotEquals(0.0d, timeBasedWindow.getErrorRate(), 0.000001);
    timeBasedWindow.clear();
    Assertions.assertEquals(0.0d, timeBasedWindow.getErrorRate(), 0.000001);
  }

}
