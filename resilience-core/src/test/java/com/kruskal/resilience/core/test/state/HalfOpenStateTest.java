package com.kruskal.resilience.core.test.state;

import com.kruskal.resilience.core.Configuration;
import com.kruskal.resilience.core.Context;
import com.kruskal.resilience.core.StateContainer;
import com.kruskal.resilience.core.constant.RetryStrategy;
import com.kruskal.resilience.core.constant.SlidingWindowStrategy;
import com.kruskal.resilience.core.state.CloseStateHandler;
import com.kruskal.resilience.core.state.HalfOpenStateHandler;
import com.kruskal.resilience.core.state.OpenStateHandler;
import com.kruskal.resilience.core.test.testutil.FunctionalUtil;
import com.kruskal.resilience.core.test.testutil.TestStateContainer;
import com.kruskal.resilience.core.window.CountBasedWindow;
import com.kruskal.resilience.core.window.SlidingWindow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class HalfOpenStateTest {


  private final double ERROR_THRESHOLD = 0.5;
  private final int WINDOW_SIZE = 10;
  private final int MIN_CALL_TO_EVALUATE = 3;
  private final int NUMBER_OF_RETRY = 5;
  private long WAIT_DURATION_IN_OPEN_STATE = 200;

  private Context context;
  private SlidingWindow slidingWindow;
  private StateContainer stateContainer;
  private HalfOpenStateHandler stateHandler;

  @Test
  public void retryAndSuccessTest() {
    this.init();
    int maxAcceptableError = (int) ((Math.ceil(ERROR_THRESHOLD * NUMBER_OF_RETRY - 1)));
    int shouldSuccessAttempt = NUMBER_OF_RETRY - maxAcceptableError;
    for(int i = 0; i < shouldSuccessAttempt; i++){
      stateHandler.execute(FunctionalUtil.doNothingRunnable());
    }

    Assertions.assertTrue(stateHandler.acquirePermission());
    Assertions.assertSame(stateHandler, stateContainer.getStateHandler());

    for(int i = 0; i < maxAcceptableError; i++){
      stateHandler.execute(FunctionalUtil.throwErrorRunnable());
    }

    Assertions.assertNotSame(stateHandler, stateContainer.getStateHandler());
    Assertions.assertTrue(stateContainer.getStateHandler().acquirePermission());
    Assertions.assertTrue(stateContainer.getStateHandler() instanceof CloseStateHandler);
    Assertions.assertEquals(0, slidingWindow.getErrorRate(), 0.000001);
  }

  @Test
  public void retryAndFailedTest() {
    this.init();

    this.init();
    int minRequiredError = (int) ((Math.floor(ERROR_THRESHOLD * NUMBER_OF_RETRY + 1)));
    int shouldSuccessAttempt = NUMBER_OF_RETRY - minRequiredError;
    for(int i = 0; i < shouldSuccessAttempt; i++){
      stateHandler.execute(FunctionalUtil.doNothingRunnable());
    }

    Assertions.assertTrue(stateHandler.acquirePermission());
    Assertions.assertSame(stateHandler, stateContainer.getStateHandler());

    for(int i = 0; i < minRequiredError; i++){
      stateHandler.execute(FunctionalUtil.throwErrorRunnable());
    }

    Assertions.assertNotSame(stateHandler, stateContainer.getStateHandler());
    Assertions.assertFalse(stateContainer.getStateHandler().acquirePermission());
    Assertions.assertTrue(stateContainer.getStateHandler() instanceof OpenStateHandler);
  }

  private void init(){
    context = new Context();
    Configuration configuration = new Configuration();
    configuration.setSlidingWindowStrategy(SlidingWindowStrategy.COUNT_BASED);
    configuration.setSlidingWindowSize(WINDOW_SIZE);
    configuration.setErrorThreshold(ERROR_THRESHOLD);
    configuration.setMinimumCallToEvaluate(MIN_CALL_TO_EVALUATE);
    configuration.setWaitDurationInOpenState(WAIT_DURATION_IN_OPEN_STATE);
    configuration.setRetryStrategy(RetryStrategy.OPTIMISTIC);
    configuration.setNumberOfRetryInHalfOpenState(NUMBER_OF_RETRY);

    slidingWindow = new CountBasedWindow(configuration);
    // init sliding window state
    Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
      while(slidingWindow.getErrorRate() < ERROR_THRESHOLD){
        slidingWindow.ackAttempt(false);
      }
    });

    context.setConfiguration(configuration);
    context.setSlidingWindow(slidingWindow);


    stateContainer = new TestStateContainer();

    stateHandler = new HalfOpenStateHandler(context, stateContainer);
    stateContainer.setStateHandler(stateHandler);
  }

}
