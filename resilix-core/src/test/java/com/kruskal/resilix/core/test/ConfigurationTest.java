package com.kruskal.resilix.core.test;

import com.kruskal.resilix.core.Configuration;
import com.kruskal.resilix.core.retry.RetryStrategy;
import com.kruskal.resilix.core.window.SlidingWindowStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConfigurationTest {

  double EPSILON = 0.000001d;

  @Test
  void configurationPositiveCasesTest() {
    Configuration configuration = new Configuration();

    configuration.setErrorThreshold(0.001d);
    Assertions.assertEquals(0.001d, configuration.getErrorThreshold(), EPSILON);
    configuration.setErrorThreshold(0.999);
    Assertions.assertEquals(0.999, configuration.getErrorThreshold(), EPSILON);

    configuration.setSlidingWindowStrategy(SlidingWindowStrategy.COUNT_BASED);
    Assertions.assertEquals(SlidingWindowStrategy.COUNT_BASED, configuration.getSlidingWindowStrategy());
    configuration.setSlidingWindowStrategy(SlidingWindowStrategy.TIME_BASED);
    Assertions.assertEquals(SlidingWindowStrategy.TIME_BASED, configuration.getSlidingWindowStrategy());

    configuration.setSlidingWindowMaxSize(1);
    Assertions.assertEquals(1, configuration.getSlidingWindowMaxSize());
    configuration.setSlidingWindowMaxSize(1000);
    Assertions.assertEquals(1000, configuration.getSlidingWindowMaxSize());

    configuration.setSlidingWindowTimeRange(1L);
    Assertions.assertEquals(1L, configuration.getWindowTimeRange());
    configuration.setSlidingWindowTimeRange(1000);
    Assertions.assertEquals(1000L, configuration.getWindowTimeRange());

    configuration.setMinimumCallToEvaluate(0);
    Assertions.assertEquals(0, configuration.getMinimumCallToEvaluate());
    configuration.setMinimumCallToEvaluate(1);
    Assertions.assertEquals(1, configuration.getMinimumCallToEvaluate());

    configuration.setWaitDurationInOpenState(0L);
    Assertions.assertEquals(0L, configuration.getWaitDurationInOpenState());
    configuration.setWaitDurationInOpenState(1000L);
    Assertions.assertEquals(1000L, configuration.getWaitDurationInOpenState());

    configuration.setRetryStrategy(RetryStrategy.OPTIMISTIC);
    Assertions.assertEquals(RetryStrategy.OPTIMISTIC, configuration.getRetryStrategy());
    configuration.setRetryStrategy(RetryStrategy.PESSIMISTIC);
    Assertions.assertEquals(RetryStrategy.PESSIMISTIC, configuration.getRetryStrategy());

    configuration.setNumberOfRetryInHalfOpenState(1);
    Assertions.assertEquals(1, configuration.getNumberOfRetryInHalfOpenState());
    configuration.setNumberOfRetryInHalfOpenState(10);
    Assertions.assertEquals(10, configuration.getNumberOfRetryInHalfOpenState());

  }

  @Test
  void negativeCasesTest(){
    Configuration configuration = new Configuration();

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> configuration.setErrorThreshold(-0.001d)
    );
    Assertions.assertThrows(IllegalArgumentException.class,
        () -> configuration.setErrorThreshold(1.001d)
    );

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> configuration.setSlidingWindowStrategy(null)
    );

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> configuration.setSlidingWindowMaxSize(0)
    );
    Assertions.assertThrows(IllegalArgumentException.class,
        () -> configuration.setSlidingWindowMaxSize(-1)
    );

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> configuration.setSlidingWindowTimeRange(0L)
    );

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> configuration.setSlidingWindowTimeRange(-1L)
    );

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> configuration.setMinimumCallToEvaluate(-1)
    );

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> configuration.setErrorThreshold(1.001d)
    );

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> configuration.setWaitDurationInOpenState(-1L)
    );

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> configuration.setRetryStrategy(null)
    );

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> configuration.setNumberOfRetryInHalfOpenState(0)
    );

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> configuration.setNumberOfRetryInHalfOpenState(-1)
    );


  }

}
