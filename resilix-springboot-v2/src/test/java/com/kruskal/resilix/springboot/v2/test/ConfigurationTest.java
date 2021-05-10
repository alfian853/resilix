package com.kruskal.resilix.springboot.v2.test;

import com.kruskal.resilix.core.Configuration;
import com.kruskal.resilix.core.retry.RetryStrategy;
import com.kruskal.resilix.core.window.SlidingWindowStrategy;
import com.kruskal.resilix.springboot.v2.EnableResilix;
import com.kruskal.resilix.springboot.v2.ResilixProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@EnableResilix
@ActiveProfiles("test")
@SpringBootTest(
    classes = {TestApplication.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class ConfigurationTest {


  @Autowired
  private ResilixProperties resilixProperties;

  @Test
  void test(){
    Assertions.assertEquals(2, resilixProperties.getConfig().entrySet().size());

    Configuration configuration1 = resilixProperties.getConfig().get("context1");

    Assertions.assertEquals(SlidingWindowStrategy.TIME_BASED, configuration1.getSlidingWindowStrategy());
    Assertions.assertEquals(RetryStrategy.PESSIMISTIC, configuration1.getRetryStrategy());
    Assertions.assertEquals(5432, configuration1.getWindowTimeRange());
    Assertions.assertEquals(23, configuration1.getSlidingWindowMaxSize());
    Assertions.assertEquals(13, configuration1.getMinimumCallToEvaluate());
    Assertions.assertEquals(0.19, configuration1.getErrorThreshold(), 0.000001);
    Assertions.assertEquals(6543, configuration1.getWaitDurationInOpenState());
    Assertions.assertEquals(7, configuration1.getNumberOfRetryInHalfOpenState());
  }

}
