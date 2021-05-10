package com.kruskal.resilix.core;

import com.kruskal.resilix.core.factory.SlidingWindowFactory;
import com.kruskal.resilix.core.window.SlidingWindow;

import java.util.HashMap;
import java.util.Map;

public class ResilixRegistry {

  private final Map<String, ResilixService> resilixServicehMap = new HashMap<>();


  public ResilixService getResilixService(String contextKey){

    if(resilixServicehMap.containsKey(contextKey)){
      return resilixServicehMap.get(contextKey);
    }
    else {
      return this.register(contextKey, new Configuration());
    }
  }

  public ResilixService register(String contextKey, Configuration configuration) {
    Context context = new Context();
    context.setContextName(contextKey);
    SlidingWindow slidingWindow = SlidingWindowFactory.create(configuration);

    context.setConfiguration(configuration);
    context.setSlidingWindow(slidingWindow);

    ResilixService resilixService = new ResilixProxy(context);
    resilixServicehMap.put(contextKey, resilixService);

    return resilixService;
  }

}
