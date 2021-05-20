package com.kruskal.resilix.springboot.v1.test;

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
@ActiveProfiles("no-config-profile")
@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = {TestApplication.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class EmptyConfigurationTest {

  @Autowired
  private ResilixProperties resilixProperties;

  @Test
  public void test(){
    Assert.assertNull(resilixProperties.getConfig());
  }

}
