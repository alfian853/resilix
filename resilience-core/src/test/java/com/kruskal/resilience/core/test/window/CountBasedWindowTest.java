package com.kruskal.resilience.core.test.window;

import com.kruskal.resilience.core.Configuration;
import com.kruskal.resilience.core.Context;
import com.kruskal.resilience.core.window.CountBasedWindow;
import com.kruskal.resilience.core.window.SlidingWindowObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class CountBasedWindowTest {

  private final Configuration configuration = new Configuration();
  private final CountBasedWindow countBasedWindow = new CountBasedWindow(configuration);

  @BeforeEach
  public void init(){
    configuration.setSlidingWindowMaxSize(10);
  }

  private boolean getRandomBoolean() {
    Random random = new Random();
    return random.ints(1, 10)
        .findFirst()
        .getAsInt() % 2 == 0;
  }

  @Test
  @DisplayName("testcase: observe 3 ack, and then unobserve and fire 5 ack")
  public void observerNotificationTest(){
    AtomicInteger count = new AtomicInteger(0);

    SlidingWindowObserver observer = success -> count.incrementAndGet();
    countBasedWindow.addObserver(observer);

    for(int i = 0; i < 3; i++){
      countBasedWindow.ackAttempt(getRandomBoolean());
    }

    countBasedWindow.removeObserver(observer);

    for(int i = 0; i < 5; i++){
      countBasedWindow.ackAttempt(getRandomBoolean());
    }

    Assertions.assertEquals(3, count.get());
  }

  @Test
  @DisplayName("testcase: fire with 25 random ack followed by 10(70% success) ack in arbitrary order")
  public void endingTest(){


    for(int i = 0; i < 25; i++){
      countBasedWindow.ackAttempt(getRandomBoolean());
    }

    int nSuccess = 7;
    int nFailure = 3;

    while(nSuccess > 0 || nFailure > 0){
      if(getRandomBoolean() || nFailure == 0){
        nSuccess--;
        countBasedWindow.ackAttempt(true);
      }
      else {
        nFailure--;
        countBasedWindow.ackAttempt(false);
      }
    }

    double errorRate = countBasedWindow.getErrorRate();

    Assertions.assertEquals(0.3, errorRate, 0.0001);

  }

}
