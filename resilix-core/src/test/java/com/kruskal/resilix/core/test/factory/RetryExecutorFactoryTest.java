package com.kruskal.resilix.core.test.factory;

import com.kruskal.resilix.core.Configuration;
import com.kruskal.resilix.core.Context;
import com.kruskal.resilix.core.retry.RetryStrategy;
import com.kruskal.resilix.core.factory.RetryExecutorFactory;
import com.kruskal.resilix.core.retry.OptimisticRetryExecutor;
import com.kruskal.resilix.core.retry.RetryExecutor;
import com.kruskal.resilix.core.window.CountBasedWindow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RetryExecutorFactoryTest {


  @Test
  @DisplayName("Create OPTIMISTIC RetryExecutor Object")
  void create_TIME_BASED_test(){
    Configuration configuration = new Configuration();
    configuration.setRetryStrategy(RetryStrategy.OPTIMISTIC);

    Context context = new Context();
    context.setConfiguration(configuration);
    context.setSlidingWindow(new CountBasedWindow(configuration));

    RetryExecutor retryExecutor = RetryExecutorFactory.create(context);

    Assertions.assertTrue(retryExecutor instanceof OptimisticRetryExecutor);

  }

  @Test
  @DisplayName("Create PESSIMISTIC RetryExecutor Object")
  void create_COUNT_BASED_test(){
    Configuration configuration = new Configuration();
    configuration.setRetryStrategy(RetryStrategy.PESSIMISTIC);

    Context context = new Context();
    context.setConfiguration(configuration);
    context.setSlidingWindow(new CountBasedWindow(configuration));

    RetryExecutor retryExecutor = RetryExecutorFactory.create(context);

    Assertions.assertTrue(retryExecutor instanceof OptimisticRetryExecutor);

  }

  @Test
  void errorCaseTest(){
    Configuration configuration = mock(Configuration.class);

    when(configuration.getRetryStrategy()).thenReturn(null);
    Context context = new Context();

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> RetryExecutorFactory.create(context)
    );

    context.setConfiguration(configuration);
    Assertions.assertThrows(IllegalArgumentException.class,
        () -> RetryExecutorFactory.create(context)
    );

  }

}
