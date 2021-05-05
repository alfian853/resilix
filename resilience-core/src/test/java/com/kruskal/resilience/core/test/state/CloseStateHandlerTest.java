package com.kruskal.resilience.core.test.state;

import com.kruskal.resilience.core.Configuration;
import com.kruskal.resilience.core.Context;
import com.kruskal.resilience.core.StateContainer;
import com.kruskal.resilience.core.constant.RetryStrategy;
import com.kruskal.resilience.core.constant.SlidingWindowStrategy;
import com.kruskal.resilience.core.state.CloseStateHandler;
import com.kruskal.resilience.core.state.OpenStateHandler;
import com.kruskal.resilience.core.state.StateHandler;
import com.kruskal.resilience.core.test.testutil.FunctionalUtil;
import com.kruskal.resilience.core.test.testutil.TestStateContainer;
import com.kruskal.resilience.core.window.CountBasedWindow;
import com.kruskal.resilience.core.window.SlidingWindow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CloseStateHandlerTest {

  private final double ERROR_THRESHOLD = 0.5;
  private final int WINDOW_SIZE = 10;
  private final int MIN_CALL_TO_EVALUATE = 3;

  private Context context;
  private StateContainer stateContainer;
  private StateHandler stateHandler;


  @BeforeEach
  public void init(){
    context = new Context();
    Configuration configuration = new Configuration();
    configuration.setErrorThreshold(ERROR_THRESHOLD);
    configuration.setNumberOfRetryInHalfOpenState(10);
    configuration.setSlidingWindowStrategy(SlidingWindowStrategy.COUNT_BASED);
    configuration.setRetryStrategy(RetryStrategy.PESSIMISTIC);
    configuration.setMinimumCallToEvaluate(MIN_CALL_TO_EVALUATE);
    configuration.setRetryWaitDuration(0);
    configuration.setSlidingWindowSize(WINDOW_SIZE);

    SlidingWindow slidingWindow = new CountBasedWindow(configuration);

    context.setConfiguration(configuration);
    context.setSlidingWindow(slidingWindow);


    stateContainer = new TestStateContainer();

    stateHandler = new CloseStateHandler(context, stateContainer);
    stateContainer.setStateHandler(stateHandler);

  }

  @Test
  public void minCallToEvaluateTest(){
    for(int i = 0; i < MIN_CALL_TO_EVALUATE - 1; i++){
      Assertions.assertFalse(
          stateHandler.execute(FunctionalUtil.throwErrorRunnable())
      );
      Assertions.assertTrue(stateHandler.acquirePermission());
    }
    Assertions.assertTrue(stateContainer.getStateHandler() instanceof CloseStateHandler);

    Assertions.assertFalse(
        stateHandler.execute(FunctionalUtil.throwErrorRunnable())
    );

    Assertions.assertNotSame(stateHandler, stateContainer.getStateHandler());
    Assertions.assertTrue(stateContainer.getStateHandler() instanceof OpenStateHandler);
  }

  @Test
  public void stillCloseAfterNumberOfAckTest(){
    for(int i = 0; i < WINDOW_SIZE; i++){
      Assertions.assertTrue(
          stateHandler.execute(FunctionalUtil.doNothingRunnable())
      );
    }

    Assertions.assertTrue(stateHandler.acquirePermission());
    Assertions.assertSame(stateHandler, stateContainer.getStateHandler());

    int errorAttempt = (int) Math.ceil(WINDOW_SIZE * (1 - ERROR_THRESHOLD)) - 1;
    for(int i = 0; i < errorAttempt; i++){
      Assertions.assertFalse(
          stateHandler.execute(FunctionalUtil.throwErrorRunnable())
      );
      Assertions.assertTrue(stateHandler.acquirePermission());
    }

    Assertions.assertSame(stateHandler, stateContainer.getStateHandler());
  }

  /**
   * it's unfortunate case when the first attempt is failed, consequently it will move
   * to close state immediately. need improvement on this.
   * */
  @Test
  public void moveToCloseStateTest(){
    stateHandler.execute(FunctionalUtil.throwErrorRunnable());
    Assertions.assertNotSame(stateHandler, stateContainer.getStateHandler());
    Assertions.assertTrue(stateContainer.getStateHandler() instanceof OpenStateHandler);
  }

  @Test
  public void moveToOpenStateTest(){
    for(int i = 0; i < WINDOW_SIZE; i++){
      Assertions.assertTrue(
          stateHandler.execute(FunctionalUtil.doNothingRunnable())
      );
    }

    Assertions.assertTrue(stateHandler.acquirePermission());
    Assertions.assertSame(stateHandler, stateContainer.getStateHandler());

    int errorAttempt = (int) Math.ceil(WINDOW_SIZE * (1 - ERROR_THRESHOLD));
    for(int i = 0; i < errorAttempt; i++){
      Assertions.assertTrue(stateHandler.acquirePermission());
      Assertions.assertFalse(
          stateHandler.execute(FunctionalUtil.throwErrorRunnable())
      );
    }
    Assertions.assertFalse(stateHandler.acquirePermission());
    Assertions.assertNotSame(stateHandler, stateContainer.getStateHandler());
    Assertions.assertTrue(stateContainer.getStateHandler() instanceof OpenStateHandler);
  }


}
