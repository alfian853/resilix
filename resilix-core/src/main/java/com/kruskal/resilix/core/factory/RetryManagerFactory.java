package com.kruskal.resilix.core.factory;

import com.kruskal.resilix.core.Context;
import com.kruskal.resilix.core.retry.OptimisticRetryManager;
import com.kruskal.resilix.core.retry.PessimisticRetryManager;
import com.kruskal.resilix.core.retry.RetryManager;

public class RetryManagerFactory {

  private RetryManagerFactory() { }

  public static RetryManager create(Context context){
    if(context.getConfiguration() != null && context.getConfiguration().getRetryStrategy() != null){
      switch (context.getConfiguration().getRetryStrategy()){
        case PESSIMISTIC:
          return new PessimisticRetryManager(context);
        case OPTIMISTIC:
          return new OptimisticRetryManager(context);
      }
    }

    throw new IllegalArgumentException("Retry strategy is not specified!");
  }
}
