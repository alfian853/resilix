package com.kruskal.resilix.springboot.v2.test;

import com.kruskal.resilix.springboot.v2.EnableResilix;
import com.kruskal.resilix.springboot.v2.ResilixProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@EnableResilix
@ActiveProfiles("no-config-profile")
@SpringBootTest(
    classes = {TestApplication.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class EmptyConfigurationTest {


  @Autowired
  private ResilixProperties resilixProperties;

  @Test
  void test(){
    Assertions.assertNull(resilixProperties.getConfig());
  }

}
