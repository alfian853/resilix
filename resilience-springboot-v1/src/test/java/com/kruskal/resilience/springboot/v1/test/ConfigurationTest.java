package com.kruskal.resilience.springboot.v1.test;

import com.kruskal.resilience.core.Configuration;
import com.kruskal.resilience.core.retry.RetryStrategy;
import com.kruskal.resilience.core.window.SlidingWindowStrategy;
import com.kruskal.resilience.springboot.v1.EnableResilience;
import com.kruskal.resilience.springboot.v1.ResilienceProperties;
import org.junit.Assert;
import org.junit.Test;
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
    Assert.assertEquals(2, resilienceProperties.getConfig().entrySet().size());

    Configuration configuration1 = resilienceProperties.getConfig().get("context1");

    Assert.assertEquals(SlidingWindowStrategy.TIME_BASED, configuration1.getSlidingWindowStrategy());
    Assert.assertEquals(RetryStrategy.PESSIMISTIC, configuration1.getRetryStrategy());
    Assert.assertEquals(5432, configuration1.getWindowTimeRange());
    Assert.assertEquals(23, configuration1.getSlidingWindowMaxSize());
    Assert.assertEquals(13, configuration1.getMinimumCallToEvaluate());
    Assert.assertEquals(0.19, configuration1.getErrorThreshold(), 0.000001);
    Assert.assertEquals(6543, configuration1.getWaitDurationInOpenState());
    Assert.assertEquals(7, configuration1.getNumberOfRetryInHalfOpenState());
  }

}
