package com.kruskal.resilix.springboot.v1;


import com.kruskal.resilix.core.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "kruskal.resilix")
public class ResilixProperties {

  private Map<String, Configuration> config;

  public Map<String, Configuration> getConfig() {
    return config;
  }

  public void setConfig(Map<String, Configuration> config) {
    this.config = config;
  }

}
