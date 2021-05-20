package com.kruskal.resilix.springboot.v1;

import com.kruskal.resilix.core.ResilixRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan({"com.kruskal.resilix.springboot.v1"})
@EnableConfigurationProperties(ResilixProperties.class)
public class ResilixConfiguration {

  @Bean
  ResilixRegistry resilixRegistry(ResilixProperties resilixProperties){
    ResilixRegistry registry = new ResilixRegistry();

    if(resilixProperties.getConfig() != null){
      resilixProperties.getConfig().entrySet().forEach(configEntry ->
          registry.register(configEntry.getKey(), configEntry.getValue())
      );
    }

    return registry;
  }

  @Bean
  ResilixWatcherAspect resilixWatcherAspect(ResilixRegistry resilixRegistry){
    return new ResilixWatcherAspect(resilixRegistry);
  }

}
