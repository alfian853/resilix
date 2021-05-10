package com.kruskal.resilix.core.factory;

import com.kruskal.resilix.core.Configuration;
import com.kruskal.resilix.core.window.CountBasedWindow;
import com.kruskal.resilix.core.window.SlidingWindow;
import com.kruskal.resilix.core.window.TimeBasedWindow;

public class SlidingWindowFactory {

  private SlidingWindowFactory() { }

  public static SlidingWindow create(Configuration configuration){
    if(configuration.getSlidingWindowStrategy() != null){
      switch (configuration.getSlidingWindowStrategy()){
        case TIME_BASED:
          return new TimeBasedWindow(configuration);
        case COUNT_BASED:
          return new CountBasedWindow(configuration);
      }
    }

    throw new IllegalArgumentException("Retry strategy is not specified!");
  }

}
