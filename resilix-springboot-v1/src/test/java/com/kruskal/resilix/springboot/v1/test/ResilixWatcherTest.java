package com.kruskal.resilix.springboot.v1.test;

import com.kruskal.resilix.core.Configuration;
import com.kruskal.resilix.core.ResilixExecutor;
import com.kruskal.resilix.core.ResilixProxy;
import com.kruskal.resilix.core.ResilixRegistry;
import com.kruskal.resilix.core.state.CloseStateHandler;
import com.kruskal.resilix.core.window.SlidingWindow;
import com.kruskal.resilix.springboot.v1.EnableResilix;
import com.kruskal.resilix.springboot.v1.ResilixProperties;
import com.kruskal.resilix.springboot.v1.test.testutil.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

@EnableResilix
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = {TestApplication.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class ResilixWatcherTest {


  @Autowired
  DummyService dummyService;

  @Autowired
  private ResilixRegistry resilixRegistry;

  @Autowired
  private ResilixProperties resilixProperties;

  @Test
  public void aspectTest() throws NoSuchFieldException, IllegalAccessException {

    ResilixExecutor resilixExecutor = resilixRegistry.getResilixExecutor(DefaultConst.contextKey);
    ResilixProxy resilixProxy = (ResilixProxy) resilixExecutor;
    CloseStateHandler stateHandler = (CloseStateHandler) resilixProxy.getStateHandler();

    Field swField = stateHandler.getClass().getSuperclass().getDeclaredField("slidingWindow");
    swField.setAccessible(true);
    SlidingWindow slidingWindow = (SlidingWindow) swField.get(stateHandler);

    Configuration configuration = resilixProperties.getConfig().get(DefaultConst.contextKey);

    for(int i = 0; i < configuration.getSlidingWindowMaxSize(); i++){
      Assert.assertEquals(100, dummyService.mockingMethod(100).intValue());
      AtomicBoolean executed = new AtomicBoolean(false);

      dummyService.tryVoid(() -> executed.set(true));
      Assert.assertTrue(executed.get());
    }

    Assert.assertEquals(0.0d, slidingWindow.getErrorRate(), 0.000001);

    int minFailure =
        (int) Math.ceil(configuration.getErrorThreshold() * configuration.getSlidingWindowMaxSize());

    while (minFailure-- > 0){
      int flag = minFailure % 3;
      if(flag == 0){
        AssertionUtil.assertCheckedExecution(() -> dummyService.tryGetInt100Error(), CustomTestException.class);
      }
      else if(flag == 1){
        AssertionUtil.assertExecution(() -> dummyService.tryVoidError(), CustomTestRuntimeException.class);
      }
      else {
        AssertionUtil.assertExecution(() -> dummyService.tryGetInt100ErrorRuntime(), CustomTestRuntimeException.class);
      }
    }

    Assert.assertTrue(slidingWindow.getErrorRate() >= configuration.getErrorThreshold());
  }

}
