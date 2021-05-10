package com.kruskal.resilix.springboot.v1.test;

import com.kruskal.resilix.core.Configuration;
import com.kruskal.resilix.core.retry.RetryStrategy;
import com.kruskal.resilix.core.window.SlidingWindowStrategy;
import com.kruskal.resilix.springboot.v1.EnableResilix;
import com.kruskal.resilix.springboot.v1.ResilixProperties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@EnableResilix
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = {TestApplication.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class ConfigurationTest {

  @Autowired
  private ResilixProperties resilixProperties;

  @Test
  public void test(){
    Assert.assertEquals(2, resilixProperties.getConfig().entrySet().size());

    Configuration configuration1 = resilixProperties.getConfig().get("context1");

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
