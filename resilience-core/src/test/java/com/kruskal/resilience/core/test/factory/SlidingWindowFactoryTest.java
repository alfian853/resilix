package com.kruskal.resilience.core.test.factory;

import com.kruskal.resilience.core.Configuration;
import com.kruskal.resilience.core.constant.SlidingWindowStrategy;
import com.kruskal.resilience.core.factory.SlidingWindowFactory;
import com.kruskal.resilience.core.window.CountBasedWindow;
import com.kruskal.resilience.core.window.SlidingWindow;
import com.kruskal.resilience.core.window.TimeBasedWindow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SlidingWindowFactoryTest {


  @Test
  @DisplayName("Create TIME_BASED SlidingWindow Object")
  public void create_TIME_BASED(){
    Configuration configuration = new Configuration();
    configuration.setSlidingWindowStrategy(SlidingWindowStrategy.TIME_BASED);

    SlidingWindow slidingWindow = SlidingWindowFactory.create(configuration);

    Assertions.assertTrue(slidingWindow instanceof TimeBasedWindow);
  }

  @Test
  @DisplayName("Create COUNT_BASED SlidingWindow Object")
  public void create_COUNT_BASED(){
    Configuration configuration = new Configuration();
    configuration.setSlidingWindowStrategy(SlidingWindowStrategy.COUNT_BASED);

    SlidingWindow slidingWindow = SlidingWindowFactory.create(configuration);

    Assertions.assertTrue(slidingWindow instanceof CountBasedWindow);
  }

}
