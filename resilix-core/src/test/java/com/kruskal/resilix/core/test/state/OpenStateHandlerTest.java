package com.kruskal.resilix.core.test.state;

import com.kruskal.resilix.core.Configuration;
import com.kruskal.resilix.core.Context;
import com.kruskal.resilix.core.StateContainer;
import com.kruskal.resilix.core.retry.RetryStrategy;
import com.kruskal.resilix.core.test.testutil.FunctionalUtil;
import com.kruskal.resilix.core.window.SlidingWindowStrategy;
import com.kruskal.resilix.core.state.HalfOpenStateHandler;
import com.kruskal.resilix.core.state.OpenStateHandler;
import com.kruskal.resilix.core.test.testutil.TestStateContainer;
import com.kruskal.resilix.core.window.CountBasedWindow;
import com.kruskal.resilix.core.window.SlidingWindow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class OpenStateHandlerTest {

  private final double ERROR_THRESHOLD = 0.5;
  private final int WINDOW_SIZE = 10;
  private final int MIN_CALL_TO_EVALUATE = 3;
  private long WAIT_DURATION_IN_OPEN_STATE = 200;
  private final String CONTEXT_NAME = "contextName";
  private final CountDownLatch waiter = new CountDownLatch(1);

  private Context context;
  private SlidingWindow slidingWindow;
  private StateContainer stateContainer;
  private OpenStateHandler stateHandler;

  @Test
  void movingStateAfterWaitingDurationPassedTest() throws InterruptedException {
    this.init();

    double initialErrorRate = slidingWindow.getErrorRate();
    Assertions.assertNotEquals(1.0d, initialErrorRate, 0.000001);

    for(int i = 0; i < 5; i++){
      slidingWindow.ackAttempt(false);
    }

    Assertions.assertEquals(initialErrorRate, slidingWindow.getErrorRate(), 0.000001);

    stateHandler.evaluateState();
    Assertions.assertSame(stateHandler, stateContainer.getStateHandler());

    waiter.await(WAIT_DURATION_IN_OPEN_STATE, TimeUnit.MILLISECONDS);

    Assertions.assertTrue(stateHandler.acquirePermission());
    Assertions.assertNotSame(stateHandler, stateContainer.getStateHandler());
    Assertions.assertTrue(stateContainer.getStateHandler() instanceof HalfOpenStateHandler);
    Assertions.assertEquals(CONTEXT_NAME, context.getContextName());
  }

  @Test
  void slidingWindowRegardingTest() throws Throwable {
    WAIT_DURATION_IN_OPEN_STATE = 2000000L;
    this.init();

    double initialErrorRate = slidingWindow.getErrorRate();

    for(int i = 0; i < WINDOW_SIZE; i++){
      Assertions.assertFalse(
          stateHandler.executeChecked(FunctionalUtil.throwErrorCheckedRunnable())
      );
    }

    // ack should be ignored, thus the error rate doesn't increase
    Assertions.assertEquals(initialErrorRate, slidingWindow.getErrorRate(), 0.001);

  }

  private void init(){
    context = new Context();
    Configuration configuration = new Configuration();
    configuration.setSlidingWindowStrategy(SlidingWindowStrategy.COUNT_BASED);
    configuration.setSlidingWindowMaxSize(WINDOW_SIZE);
    configuration.setErrorThreshold(ERROR_THRESHOLD);
    configuration.setMinimumCallToEvaluate(MIN_CALL_TO_EVALUATE);
    configuration.setWaitDurationInOpenState(WAIT_DURATION_IN_OPEN_STATE);
    configuration.setRetryStrategy(RetryStrategy.OPTIMISTIC);
    configuration.setNumberOfRetryInHalfOpenState(10);

    slidingWindow = new CountBasedWindow(configuration);
    // init sliding window state
    Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
      while(slidingWindow.getErrorRate() < ERROR_THRESHOLD){
        slidingWindow.ackAttempt(false);
      }
    });

    context.setConfiguration(configuration);
    context.setSlidingWindow(slidingWindow);
    context.setContextName(CONTEXT_NAME);


    stateContainer = new TestStateContainer();

    stateHandler = new OpenStateHandler(context, stateContainer);
    stateContainer.setStateHandler(stateHandler);
  }


}
