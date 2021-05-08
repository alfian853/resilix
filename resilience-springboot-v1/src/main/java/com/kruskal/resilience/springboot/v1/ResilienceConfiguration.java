package com.kruskal.resilience.springboot.v1;

import com.kruskal.resilience.core.ResilienceRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan({"com.kruskal.resilience.springboot.v1"})
@EnableConfigurationProperties(ResilienceProperties.class)
public class ResilienceConfiguration {

  @Bean
  ResilienceRegistry resilienceRegistry(ResilienceProperties resilienceProperties){
    ResilienceRegistry registry = new ResilienceRegistry();

    resilienceProperties.getConfig().entrySet().forEach(configEntry ->
        registry.register(configEntry.getKey(), configEntry.getValue())
    );

    return registry;
  }

}
