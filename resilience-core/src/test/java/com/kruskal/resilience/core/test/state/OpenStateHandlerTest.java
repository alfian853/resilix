package com.kruskal.resilience.core.test.state;

import com.kruskal.resilience.core.Configuration;
import com.kruskal.resilience.core.Context;
import com.kruskal.resilience.core.StateContainer;
import com.kruskal.resilience.core.retry.RetryStrategy;
import com.kruskal.resilience.core.window.SlidingWindowStrategy;
import com.kruskal.resilience.core.state.HalfOpenStateHandler;
import com.kruskal.resilience.core.state.OpenStateHandler;
import com.kruskal.resilience.core.test.testutil.TestStateContainer;
import com.kruskal.resilience.core.window.CountBasedWindow;
import com.kruskal.resilience.core.window.SlidingWindow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class OpenStateHandlerTest {

  private final double ERROR_THRESHOLD = 0.5;
  private final int WINDOW_SIZE = 10;
  private final int MIN_CALL_TO_EVALUATE = 3;
  private long WAIT_DURATION_IN_OPEN_STATE = 200;

  private Context context;
  private SlidingWindow slidingWindow;
  private StateContainer stateContainer;
  private OpenStateHandler stateHandler;

  @Test
  public void movingStateAfterWaitingDurationPassedTest() throws InterruptedException {
    this.init();

    stateHandler.evaluateState();
    Assertions.assertSame(stateHandler, stateContainer.getStateHandler());

    Thread.sleep(WAIT_DURATION_IN_OPEN_STATE);

    Assertions.assertTrue(stateHandler.acquirePermission());
    Assertions.assertNotSame(stateHandler, stateContainer.getStateHandler());
    Assertions.assertTrue(stateContainer.getStateHandler() instanceof HalfOpenStateHandler);
  }

  @Test
  public void slidingWindowRegardingTest() {
    WAIT_DURATION_IN_OPEN_STATE = 2000000L;
    this.init();

    double initialErrorRate = slidingWindow.getErrorRate();

    for(int i = 0; i < WINDOW_SIZE; i++){
      slidingWindow.ackAttempt(false);
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


    stateContainer = new TestStateContainer();

    stateHandler = new OpenStateHandler(context, stateContainer);
    stateContainer.setStateHandler(stateHandler);
  }


}
