package com.kruskal.resilix.core.test.factory;

import com.kruskal.resilix.core.Configuration;
import com.kruskal.resilix.core.factory.SlidingWindowFactory;
import com.kruskal.resilix.core.window.CountBasedWindow;
import com.kruskal.resilix.core.window.SlidingWindow;
import com.kruskal.resilix.core.window.SlidingWindowStrategy;
import com.kruskal.resilix.core.window.TimeBasedWindow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SlidingWindowFactoryTest {


  @Test
  @DisplayName("Create TIME_BASED SlidingWindow Object")
  void create_TIME_BASED(){
    Configuration configuration = new Configuration();
    configuration.setSlidingWindowStrategy(SlidingWindowStrategy.TIME_BASED);

    SlidingWindow slidingWindow = SlidingWindowFactory.create(configuration);

    Assertions.assertTrue(slidingWindow instanceof TimeBasedWindow);
  }

  @Test
  @DisplayName("Create COUNT_BASED SlidingWindow Object")
  void create_COUNT_BASED(){
    Configuration configuration = new Configuration();
    configuration.setSlidingWindowStrategy(SlidingWindowStrategy.COUNT_BASED);

    SlidingWindow slidingWindow = SlidingWindowFactory.create(configuration);

    Assertions.assertTrue(slidingWindow instanceof CountBasedWindow);
  }

  @Test
  void errorCaseTest(){
    Configuration configuration = mock(Configuration.class);
    when(configuration.getSlidingWindowStrategy()).thenReturn(null);

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> SlidingWindowFactory.create(configuration)
    );
  }


}
