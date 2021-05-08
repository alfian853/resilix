package com.kruskal.resilience.springboot.v2;


import com.kruskal.resilience.core.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "kruskal.resilience")
public class ResilienceProperties {


  private Map<String, Configuration> config;

  public Map<String, Configuration> getConfig() {
    return config;
  }

  public void setConfig(Map<String, Configuration> config) {
    this.config = config;
  }

}
