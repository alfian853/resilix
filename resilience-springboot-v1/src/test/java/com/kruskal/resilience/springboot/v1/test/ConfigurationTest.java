package com.kruskal.resilience.springboot.v1.test;

import com.kruskal.resilience.core.Configuration;
import com.kruskal.resilience.core.constant.RetryStrategy;
import com.kruskal.resilience.core.constant.SlidingWindowStrategy;
import com.kruskal.resilience.springboot.v1.EnableResilience;
import com.kruskal.resilience.springboot.v1.ResilienceProperties;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@EnableResilience
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
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
