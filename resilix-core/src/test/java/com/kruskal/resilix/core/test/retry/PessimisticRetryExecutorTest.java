package com.kruskal.resilix.core.test.retry;

import com.kruskal.resilix.core.Configuration;
import com.kruskal.resilix.core.Context;
import com.kruskal.resilix.core.retry.RetryExecutor;
import com.kruskal.resilix.core.retry.RetryState;
import com.kruskal.resilix.core.retry.RetryStrategy;
import com.kruskal.resilix.core.test.testutil.CustomTestException;
import com.kruskal.resilix.core.util.CheckedRunnable;
import com.kruskal.resilix.core.window.SlidingWindowStrategy;
import com.kruskal.resilix.core.retry.PessimisticRetryExecutor;
import com.kruskal.resilix.core.test.testutil.FunctionalUtil;
import com.kruskal.resilix.core.test.testutil.RandomUtil;
import com.kruskal.resilix.core.window.SlidingWindow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class PessimisticRetryExecutorTest {

  private int SLIDING_WINDOW_SIZE = 50;
  private double ERROR_THRESHOLD = 0.3;
  private int NUMBER_OF_RETRY = 100;

  private ExecutorService executor = Executors.newFixedThreadPool(16);

  private Configuration configuration;
  private Context context;
  private PessimisticRetryExecutor retryExecutor;

  @BeforeEach
  void init(){
    context = new Context();
    configuration = new Configuration();
    configuration.setRetryStrategy(RetryStrategy.PESSIMISTIC);
    configuration.setSlidingWindowStrategy(SlidingWindowStrategy.COUNT_BASED);
    configuration.setSlidingWindowMaxSize(SLIDING_WINDOW_SIZE);
    configuration.setErrorThreshold(ERROR_THRESHOLD);
    configuration.setNumberOfRetryInHalfOpenState(NUMBER_OF_RETRY);

    context.setConfiguration(configuration);
    retryExecutor = new PessimisticRetryExecutor(context);

  }

  @Test
  @DisplayName("should be REJECTED")
  void rejectedCaseTest() {
    Assertions.assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {

      Assertions.assertEquals(RetryState.ON_GOING, retryExecutor.getRetryState());
      Assertions.assertEquals(0.0d, retryExecutor.getErrorRate(), 0.000001);

      int minFailedAck = (int) (((ERROR_THRESHOLD) * NUMBER_OF_RETRY));
      int minSuccessAck = NUMBER_OF_RETRY - minFailedAck;

      List<Future<?>> futureList = new LinkedList<>();
      for (int i = 0; i < minSuccessAck; i++) {
        futureList.add(executor.submit(() -> Assertions.assertDoesNotThrow(
            () -> submitRetryableExecutor(retryExecutor, FunctionalUtil.doNothingCheckedRunnable())))
        );
      }
      futureList.stream().forEach(FunctionalUtil.doNothingConsumer());

      for (int i = 0; i < minFailedAck; i++) {
        futureList.add(executor.submit(() -> {
          Assertions.assertThrows(CustomTestException.class, () ->
              submitRetryableExecutor(retryExecutor, FunctionalUtil.throwErrorCheckedRunnable())
          );
        }));
      }
      futureList.stream().forEach(FunctionalUtil.doNothingConsumer());

      Assertions.assertTrue(retryExecutor.getErrorRate() >= ERROR_THRESHOLD);
    });

  }


  private void submitRetryableExecutor(RetryExecutor retryExecutor, CheckedRunnable checkedRunnable) throws Throwable {
    boolean executed = retryExecutor.executeChecked(checkedRunnable);

    if(!executed){
      submitRetryableExecutor(retryExecutor, checkedRunnable);
    }
  }

  @Test
  @DisplayName("should be ACCEPTED, testcase: errorThreshold=80%, success ack = 21")
  void acceptedCaseTest() {
    Assertions.assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {

      ERROR_THRESHOLD = 0.8;

      configuration.setErrorThreshold(ERROR_THRESHOLD);

      // late instantiation because the observer should be registered after populating slidingWindow's data
      retryExecutor = new PessimisticRetryExecutor(context);

      Assertions.assertEquals(RetryState.ON_GOING, retryExecutor.getRetryState());
      Assertions.assertEquals(0.0d, retryExecutor.getErrorRate(), 0.000001);

      int maxFailedAck = (int) (((ERROR_THRESHOLD) * NUMBER_OF_RETRY) - 1);
      int minSuccessAck = NUMBER_OF_RETRY - maxFailedAck;

      List<Future<?>> futureList = new LinkedList<>();
      for (int i = 0; i < minSuccessAck; i++) {
        futureList.add(executor.submit(() -> Assertions.assertDoesNotThrow(
            () -> submitRetryableExecutor(retryExecutor, FunctionalUtil.doNothingCheckedRunnable())))
        );
      }
      futureList.stream().forEach(FunctionalUtil.doNothingConsumer());

      for (int i = 0; i < maxFailedAck; i++) {
        futureList.add(executor.submit(() -> {
          Assertions.assertThrows(CustomTestException.class, () ->
              submitRetryableExecutor(retryExecutor, FunctionalUtil.throwErrorCheckedRunnable())
          );
        }));
      }

      futureList.stream().forEach(FunctionalUtil.doNothingConsumer());

      Assertions.assertTrue(retryExecutor.getErrorRate() < ERROR_THRESHOLD);

      // assert if the quota has been exceeded
      Assertions.assertFalse(retryExecutor.acquirePermission());
      Assertions.assertEquals(RetryState.ACCEPTED, retryExecutor.getRetryState());
    });
  }
}
