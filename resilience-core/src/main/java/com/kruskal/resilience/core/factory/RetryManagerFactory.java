package com.kruskal.resilience.core.factory;

import com.kruskal.resilience.core.Context;
import com.kruskal.resilience.core.retry.OptimisticRetryManager;
import com.kruskal.resilience.core.retry.PessimisticRetryManager;
import com.kruskal.resilience.core.retry.RetryManager;

public class RetryManagerFactory {

  private RetryManagerFactory() { }

  public static RetryManager create(Context context){
    switch (context.getConfiguration().getRetryStrategy()){
      case PESSIMISTIC:
        return new PessimisticRetryManager(context);
      case OPTIMISTIC:
        return new OptimisticRetryManager(context);
    }

    throw new IllegalArgumentException("Retry strategy is not specified!");
  }
}
