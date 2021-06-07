package com.kruskal.resilix.core;

import com.kruskal.resilix.core.executor.ResilixExecutor;
import com.kruskal.resilix.core.factory.SlidingWindowFactory;
import com.kruskal.resilix.core.window.SlidingWindow;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link ResilixRegistry} is a factory to create and store all {@link ResilixExecutor} instances.
 */
public class ResilixRegistry {

  private final Map<String, ResilixExecutor> resilixExecutorMap = new HashMap<>();

  /**
   * this method will provide ResilixExecutor by the contextKey.<br>
   * if it isn't exist yet, it would create a new ResilixExecutor with default {@link Configuration}.
   * @param contextKey the key for specific ResilixExecutor.
   * @return {@link ResilixExecutor} for the contextKey
   */
  public ResilixExecutor getResilixExecutor(String contextKey){

    if(resilixExecutorMap.containsKey(contextKey)){
      return resilixExecutorMap.get(contextKey);
    }
    else {
      return this.register(contextKey, new Configuration());
    }
  }


  /**
   * @param contextKey the key for ResilixExecutor
   * @param configuration is not nullable
   * @return ResilixExecutor
   */
  public ResilixExecutor register(String contextKey, Configuration configuration) {
    Context context = new Context();
    context.setContextName(contextKey);
    SlidingWindow slidingWindow = SlidingWindowFactory.create(configuration);

    context.setConfiguration(configuration);
    context.setSlidingWindow(slidingWindow);

    ResilixExecutor resilixExecutor = new ResilixProxy(context);
    resilixExecutorMap.put(contextKey, resilixExecutor);

    return resilixExecutor;
  }

}
