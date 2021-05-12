package com.kruskal.resilix.core;

import com.kruskal.resilix.core.factory.SlidingWindowFactory;
import com.kruskal.resilix.core.window.SlidingWindow;

import java.util.HashMap;
import java.util.Map;

public class ResilixRegistry {

  private final Map<String, ResilixExecutor> resilixExecutorMap = new HashMap<>();


  public ResilixExecutor getResilixExecutor(String contextKey){

    if(resilixExecutorMap.containsKey(contextKey)){
      return resilixExecutorMap.get(contextKey);
    }
    else {
      return this.register(contextKey, new Configuration());
    }
  }

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
