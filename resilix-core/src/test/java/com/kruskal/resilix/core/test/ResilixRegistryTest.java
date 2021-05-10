package com.kruskal.resilix.core.test;

import com.kruskal.resilix.core.Configuration;
import com.kruskal.resilix.core.ResilixService;
import com.kruskal.resilix.core.ResilixRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class ResilixRegistryTest {

  @Test
  void supportAutoCreateTest(){
    ResilixRegistry registry = new ResilixRegistry();

    String contextName1 = UUID.randomUUID().toString();
    String contextName2 = UUID.randomUUID().toString();

    ResilixService executor1 = registry.getResilixService(contextName1);
    Assertions.assertSame(executor1, registry.getResilixService(contextName1));

    ResilixService executor2 = registry.register(contextName2, new Configuration());
    Assertions.assertSame(executor2, registry.getResilixService(contextName2));
  }

}
