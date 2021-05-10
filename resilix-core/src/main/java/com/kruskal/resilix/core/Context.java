package com.kruskal.resilix.core;

import com.kruskal.resilix.core.window.SlidingWindow;

public class Context {

  private String contextName;
  private Configuration configuration;
  private SlidingWindow slidingWindow;

  public String getContextName() {
    return contextName;
  }

  public void setContextName(String contextName) {
    this.contextName = contextName;
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  public void setConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }

  public SlidingWindow getSlidingWindow() {
    return slidingWindow;
  }

  public void setSlidingWindow(SlidingWindow slidingWindow) {
    this.slidingWindow = slidingWindow;
  }

}
