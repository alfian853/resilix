package com.kruskal.resilix.core.test.retry;

import com.kruskal.resilix.core.Configuration;
import com.kruskal.resilix.core.Context;
import com.kruskal.resilix.core.retry.OptimisticRetryExecutor;
import com.kruskal.resilix.core.retry.RetryState;
import com.kruskal.resilix.core.retry.RetryStrategy;
import com.kruskal.resilix.core.test.testutil.CustomTestException;
import com.kruskal.resilix.core.test.testutil.FunctionalUtil;
import com.kruskal.resilix.core.window.SlidingWindowStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class OptimisticRetryExecutorTest {

  private int SLIDING_WINDOW_SIZE = 50;
  private double ERROR_THRESHOLD = 0.3;
  private int NUMBER_OF_RETRY = 100;

  private ExecutorService executor = Executors.newFixedThreadPool(16);

  private Configuration configuration;
  private Context context;
  private OptimisticRetryExecutor retryExecutor;

  @BeforeEach
  void init(){
    context = new Context();
    configuration = new Configuration();
    configuration.setRetryStrategy(RetryStrategy.OPTIMISTIC);
    configuration.setSlidingWindowStrategy(SlidingWindowStrategy.COUNT_BASED);
    configuration.setSlidingWindowMaxSize(SLIDING_WINDOW_SIZE);
    configuration.setErrorThreshold(ERROR_THRESHOLD);
    configuration.setNumberOfRetryInHalfOpenState(NUMBER_OF_RETRY);

    context.setConfiguration(configuration);
    retryExecutor = new OptimisticRetryExecutor(context);

  }

  @Test
  @DisplayName("should be REJECTED")
  void rejectedCaseTest() throws Throwable {
    // late instantiation because the observer should be registered after populating slidingWindow's data

    Assertions.assertEquals(RetryState.ON_GOING, retryExecutor.getRetryState());
    Assertions.assertEquals(0.0d, retryExecutor.getErrorRate(), 0.000001);

    int minFailedAck = (int) (((ERROR_THRESHOLD) * NUMBER_OF_RETRY)) + 2;
    int maxSuccessAck = NUMBER_OF_RETRY - minFailedAck;
    List<Future<?>> futureList = new LinkedList<>();

    for (int i = 0; i < maxSuccessAck; i++){
      futureList.add(executor.submit(() -> {
        Assertions.assertDoesNotThrow(() -> retryExecutor.executeChecked(FunctionalUtil.doNothingCheckedRunnable()));
      }));
    }

    for (int i = 0; i < minFailedAck; i++){
      futureList.add(executor.submit(() -> {
        Assertions.assertThrows(CustomTestException.class, () ->
            retryExecutor.executeChecked(FunctionalUtil.throwErrorCheckedSupplier())
        );
      }));
    }

    futureList.forEach(FunctionalUtil.doNothingConsumer());

    Assertions.assertTrue(retryExecutor.getErrorRate() >= ERROR_THRESHOLD);
    Assertions.assertFalse(retryExecutor.acquirePermission());
    Assertions.assertEquals(RetryState.REJECTED, retryExecutor.getRetryState());
    Assertions.assertFalse(retryExecutor.executeChecked(FunctionalUtil.throwErrorCheckedRunnable()));
    Assertions.assertEquals(RetryState.REJECTED, retryExecutor.getRetryState());
  }

  @Test
  @DisplayName("should be ACCEPTED, testcase: errorThreshold=80%, success ack = 21")
  void acceptedCaseTest() {
    ERROR_THRESHOLD = 0.8;
    configuration.setErrorThreshold(ERROR_THRESHOLD);
    retryExecutor = new OptimisticRetryExecutor(context);

    Assertions.assertEquals(RetryState.ON_GOING, retryExecutor.getRetryState());
    Assertions.assertEquals(0.0d, retryExecutor.getErrorRate(), 0.000001);

    int minSuccessAck = (int) (((1.0d - ERROR_THRESHOLD) * NUMBER_OF_RETRY)) + 2;

    List<Future<?>> futureList = new LinkedList<>();
    for(int i = 0; i < minSuccessAck; i++){
      futureList.add(executor.submit(() -> {
        Assertions.assertDoesNotThrow(() -> retryExecutor.executeChecked(FunctionalUtil.doNothingCheckedRunnable()));
      }));
    }
    futureList.stream().forEach(FunctionalUtil.doNothingConsumer());

    for(int i = 0; i < NUMBER_OF_RETRY - minSuccessAck; i++){
      futureList.add(executor.submit(() -> {
        Assertions.assertThrows(CustomTestException.class, () ->
            retryExecutor.executeChecked(FunctionalUtil.throwErrorCheckedSupplier())
        );
      }));
    }

    futureList.stream().forEach(FunctionalUtil.doNothingConsumer());

    Assertions.assertTrue(retryExecutor.getErrorRate() < ERROR_THRESHOLD);

    // assert if the quota has been exceeded
    Assertions.assertFalse(retryExecutor.acquirePermission());
    Assertions.assertEquals(RetryState.ACCEPTED, retryExecutor.getRetryState());
  }
}
