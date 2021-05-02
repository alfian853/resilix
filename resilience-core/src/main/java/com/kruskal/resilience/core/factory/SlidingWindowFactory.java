package com.kruskal.resilience.core.factory;

import com.kruskal.resilience.core.Configuration;
import com.kruskal.resilience.core.window.CountBasedWindow;
import com.kruskal.resilience.core.window.SlidingWindow;
import com.kruskal.resilience.core.window.TimeBasedWindow;

public class SlidingWindowFactory {

  private SlidingWindowFactory() { }

  public static SlidingWindow create(Configuration configuration){
    switch (configuration.getSlidingWindowStrategy()){
      case TIME_BASED:
        return new TimeBasedWindow(configuration);
      case COUNT_BASED:
        return new CountBasedWindow(configuration);
    }

    throw new IllegalArgumentException("Retry strategy is not specified!");
  }

}
