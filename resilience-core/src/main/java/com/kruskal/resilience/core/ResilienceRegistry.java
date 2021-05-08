package com.kruskal.resilience.core;

import com.kruskal.resilience.core.factory.SlidingWindowFactory;
import com.kruskal.resilience.core.window.SlidingWindow;

import java.util.HashMap;
import java.util.Map;

public class ResilienceRegistry {

  private final Map<String, ResilienceService> resilienceHandlerMap = new HashMap<>();


  public ResilienceService getResilienceExecutor(String contextKey){

    if(resilienceHandlerMap.containsKey(contextKey)){
      return resilienceHandlerMap.get(contextKey);
    }
    else {
      return this.register(contextKey, new Configuration());
    }
  }

  public ResilienceService register(String contextKey, Configuration configuration) {
    Context context = new Context();
    context.setContextName(contextKey);
    SlidingWindow slidingWindow = SlidingWindowFactory.create(configuration);

    context.setConfiguration(configuration);
    context.setSlidingWindow(slidingWindow);

    ResilienceService resilienceHandler = new ResilienceProxy(context);
    resilienceHandlerMap.put(contextKey, resilienceHandler);

    return resilienceHandler;
  }

}
