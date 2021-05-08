package com.kruskal.resilience.springboot.v2.test;

import com.kruskal.resilience.core.Configuration;
import com.kruskal.resilience.core.retry.RetryStrategy;
import com.kruskal.resilience.core.window.SlidingWindowStrategy;
import com.kruskal.resilience.springboot.v2.EnableResilience;
import com.kruskal.resilience.springboot.v2.ResilienceProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@EnableResilience
@ActiveProfiles("test")
@SpringBootTest(
    classes = {TestApplication.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class ConfigurationTest {


  @Autowired
  private ResilienceProperties resilienceProperties;

  @Test
  public void test(){
    Assertions.assertEquals(2, resilienceProperties.getConfig().entrySet().size());

    Configuration configuration1 = resilienceProperties.getConfig().get("context1");

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
