package com.kruskal.resilience.core.test;

import com.kruskal.resilience.core.Configuration;
import com.kruskal.resilience.core.ResilienceService;
import com.kruskal.resilience.core.ResilienceRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class ResilienceRegistryTest {

  @Test
  void supportAutoCreateTest(){
    ResilienceRegistry registry = new ResilienceRegistry();

    String contextName1 = UUID.randomUUID().toString();
    String contextName2 = UUID.randomUUID().toString();

    ResilienceService executor1 = registry.getResilienceExecutor(contextName1);
    Assertions.assertSame(executor1, registry.getResilienceExecutor(contextName1));

    ResilienceService executor2 = registry.register(contextName2, new Configuration());
    Assertions.assertSame(executor2, registry.getResilienceExecutor(contextName2));
  }

}
