package com.kruskal.resilix.core.test.factory;

import com.kruskal.resilix.core.Configuration;
import com.kruskal.resilix.core.Context;
import com.kruskal.resilix.core.retry.RetryStrategy;
import com.kruskal.resilix.core.factory.RetryManagerFactory;
import com.kruskal.resilix.core.retry.OptimisticRetryManager;
import com.kruskal.resilix.core.retry.RetryManager;
import com.kruskal.resilix.core.window.CountBasedWindow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RetryManagerFactoryTest {


  @Test
  @DisplayName("Create OPTIMISTIC RetryManager Object")
  void create_TIME_BASED_test(){
    Configuration configuration = new Configuration();
    configuration.setRetryStrategy(RetryStrategy.OPTIMISTIC);

    Context context = new Context();
    context.setConfiguration(configuration);
    context.setSlidingWindow(new CountBasedWindow(configuration));

    RetryManager retryManager = RetryManagerFactory.create(context);

    Assertions.assertTrue(retryManager instanceof OptimisticRetryManager);

  }

  @Test
  @DisplayName("Create PESSIMISTIC RetryManager Object")
  void create_COUNT_BASED_test(){
    Configuration configuration = new Configuration();
    configuration.setRetryStrategy(RetryStrategy.PESSIMISTIC);

    Context context = new Context();
    context.setConfiguration(configuration);
    context.setSlidingWindow(new CountBasedWindow(configuration));

    RetryManager retryManager = RetryManagerFactory.create(context);

    Assertions.assertTrue(retryManager instanceof OptimisticRetryManager);

  }

  @Test
  void errorCaseTest(){
    Configuration configuration = mock(Configuration.class);

    when(configuration.getRetryStrategy()).thenReturn(null);
    Context context = new Context();

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> RetryManagerFactory.create(context)
    );

    context.setConfiguration(configuration);
    Assertions.assertThrows(IllegalArgumentException.class,
        () -> RetryManagerFactory.create(context)
    );

  }

}
