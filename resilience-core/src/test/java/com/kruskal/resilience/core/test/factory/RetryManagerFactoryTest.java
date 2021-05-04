package com.kruskal.resilience.core.test.factory;

import com.kruskal.resilience.core.Configuration;
import com.kruskal.resilience.core.Context;
import com.kruskal.resilience.core.constant.RetryStrategy;
import com.kruskal.resilience.core.factory.RetryManagerFactory;
import com.kruskal.resilience.core.retry.OptimisticRetryManager;
import com.kruskal.resilience.core.retry.RetryManager;
import com.kruskal.resilience.core.window.CountBasedWindow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RetryManagerFactoryTest {


  @Test
  @DisplayName("Create OPTIMISTIC RetryManager Object")
  public void create_TIME_BASED(){
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
  public void create_COUNT_BASED(){
    Configuration configuration = new Configuration();
    configuration.setRetryStrategy(RetryStrategy.PESSIMISTIC);

    Context context = new Context();
    context.setConfiguration(configuration);
    context.setSlidingWindow(new CountBasedWindow(configuration));

    RetryManager retryManager = RetryManagerFactory.create(context);

    Assertions.assertTrue(retryManager instanceof OptimisticRetryManager);

  }

}
