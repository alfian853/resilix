package com.kruskal.resilix.core.factory;

import com.kruskal.resilix.core.Context;
import com.kruskal.resilix.core.retry.OptimisticRetryExecutor;
import com.kruskal.resilix.core.retry.PessimisticRetryExecutor;
import com.kruskal.resilix.core.retry.RetryExecutor;

public class RetryExecutorFactory {

  private RetryExecutorFactory() { }

  public static RetryExecutor create(Context context){
    if(context.getConfiguration() != null && context.getConfiguration().getRetryStrategy() != null){
      switch (context.getConfiguration().getRetryStrategy()){
        case PESSIMISTIC:
          return new PessimisticRetryExecutor(context);
        case OPTIMISTIC:
          return new OptimisticRetryExecutor(context);
      }
    }

    throw new IllegalArgumentException("Retry strategy is not specified!");
  }
}
