package com.kruskal.resilix.core.test;

import com.kruskal.resilix.core.Configuration;
import com.kruskal.resilix.core.ResilixExecutor;
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

    ResilixExecutor executor1 = registry.getResilixExecutor(contextName1);
    Assertions.assertSame(executor1, registry.getResilixExecutor(contextName1));

    ResilixExecutor executor2 = registry.register(contextName2, new Configuration());
    Assertions.assertSame(executor2, registry.getResilixExecutor(contextName2));
  }

}
