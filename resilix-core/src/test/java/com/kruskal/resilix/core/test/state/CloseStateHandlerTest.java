package com.kruskal.resilix.core.test.state;

import com.kruskal.resilix.core.Configuration;
import com.kruskal.resilix.core.Context;
import com.kruskal.resilix.core.StateContainer;
import com.kruskal.resilix.core.retry.RetryStrategy;
import com.kruskal.resilix.core.state.CloseStateHandler;
import com.kruskal.resilix.core.state.OpenStateHandler;
import com.kruskal.resilix.core.test.testutil.FunctionalUtil;
import com.kruskal.resilix.core.test.testutil.TestStateContainer;
import com.kruskal.resilix.core.window.CountBasedWindow;
import com.kruskal.resilix.core.window.SlidingWindow;
import com.kruskal.resilix.core.window.SlidingWindowStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CloseStateHandlerTest {

  private final double ERROR_THRESHOLD = 0.5;
  private final int WINDOW_SIZE = 10;
  private final int MIN_CALL_TO_EVALUATE = 3;

  private Context context;
  private StateContainer stateContainer;
  private CloseStateHandler stateHandler;


  @BeforeEach
  void init(){
    context = new Context();
    Configuration configuration = new Configuration();
    configuration.setSlidingWindowStrategy(SlidingWindowStrategy.COUNT_BASED);
    configuration.setSlidingWindowMaxSize(WINDOW_SIZE);
    configuration.setErrorThreshold(ERROR_THRESHOLD);
    configuration.setMinimumCallToEvaluate(MIN_CALL_TO_EVALUATE);
    configuration.setRetryStrategy(RetryStrategy.PESSIMISTIC);
    configuration.setNumberOfRetryInHalfOpenState(10);
    configuration.setWaitDurationInOpenState(10000000);

    SlidingWindow slidingWindow = new CountBasedWindow(configuration);

    context.setConfiguration(configuration);
    context.setSlidingWindow(slidingWindow);


    stateContainer = new TestStateContainer();

    stateHandler = new CloseStateHandler(context, stateContainer);
    stateContainer.setStateHandler(stateHandler);

  }

  @Test
  void minCallToEvaluateTest(){
    for(int i = 0; i < MIN_CALL_TO_EVALUATE - 1; i++){
      Assertions.assertThrows(RuntimeException.class,
          () -> stateHandler.execute(FunctionalUtil.throwErrorSupplier())
      );
      Assertions.assertTrue(stateHandler.acquirePermission());
    }
    Assertions.assertTrue(stateContainer.getStateHandler() instanceof CloseStateHandler);

    Assertions.assertThrows(RuntimeException.class,
        () -> stateHandler.execute(FunctionalUtil.throwErrorRunnable())
    );

    Assertions.assertNotSame(stateHandler, stateContainer.getStateHandler());
    Assertions.assertTrue(stateContainer.getStateHandler() instanceof OpenStateHandler);
  }

  @Test
  void stillCloseAfterNumberOfAckTest() {
    for(int i = 0; i < WINDOW_SIZE; i++){
      stateHandler.execute(FunctionalUtil.doNothingRunnable());
    }

    Assertions.assertTrue(stateHandler.acquirePermission());
    Assertions.assertSame(stateHandler, stateContainer.getStateHandler());

    int errorAttempt = (int) Math.ceil(WINDOW_SIZE * (1 - ERROR_THRESHOLD)) - 1;
    for(int i = 0; i < errorAttempt; i++){
      Assertions.assertTrue(stateHandler.execute(FunctionalUtil.trueSupplier()).isExecuted());
      Assertions.assertTrue(stateHandler.acquirePermission());
    }

    Assertions.assertSame(stateHandler, stateContainer.getStateHandler());
  }

  @Test
  void moveToOpenStateTest() {
    for(int i = 0; i < WINDOW_SIZE; i++){
      stateHandler.execute(FunctionalUtil.doNothingRunnable());
    }

    Assertions.assertTrue(stateHandler.acquirePermission());
    Assertions.assertSame(stateHandler, stateContainer.getStateHandler());

    int errorAttempt = (int) Math.ceil(WINDOW_SIZE * (1 - ERROR_THRESHOLD));
    for(int i = 0; i < errorAttempt; i++){
      Assertions.assertTrue(stateHandler.acquirePermission());
      Assertions.assertThrows(RuntimeException.class,
          () -> stateHandler.execute(FunctionalUtil.throwErrorRunnable())
      );
    }
    Assertions.assertNotSame(stateHandler, stateContainer.getStateHandler());
    Assertions.assertFalse(stateContainer.getStateHandler().acquirePermission());
    Assertions.assertTrue(stateContainer.getStateHandler() instanceof OpenStateHandler);
  }


}
