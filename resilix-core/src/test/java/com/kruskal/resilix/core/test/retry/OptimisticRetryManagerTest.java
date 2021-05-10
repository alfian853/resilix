package com.kruskal.resilix.core.test.retry;

import com.kruskal.resilix.core.Configuration;
import com.kruskal.resilix.core.Context;
import com.kruskal.resilix.core.retry.RetryState;
import com.kruskal.resilix.core.retry.RetryStrategy;
import com.kruskal.resilix.core.window.SlidingWindowStrategy;
import com.kruskal.resilix.core.retry.OptimisticRetryManager;
import com.kruskal.resilix.core.test.testutil.FunctionalUtil;
import com.kruskal.resilix.core.test.testutil.RandomUtil;
import com.kruskal.resilix.core.window.CountBasedWindow;
import com.kruskal.resilix.core.window.SlidingWindow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class OptimisticRetryManagerTest {

  private int SLIDING_WINDOW_SIZE = 50;
  private double ERROR_THRESHOLD = 0.3;
  private int NUMBER_OF_RETRY = 100;

  private ExecutorService executor = Executors.newFixedThreadPool(16);

  private Configuration configuration;
  private Context context;
  private SlidingWindow slidingWindow;
  private OptimisticRetryManager retryManager;

  @BeforeEach
  void init(){
    context = new Context();
    configuration = new Configuration();
    configuration.setRetryStrategy(RetryStrategy.OPTIMISTIC);
    configuration.setSlidingWindowStrategy(SlidingWindowStrategy.COUNT_BASED);
    configuration.setSlidingWindowMaxSize(SLIDING_WINDOW_SIZE);
    configuration.setErrorThreshold(ERROR_THRESHOLD);
    configuration.setNumberOfRetryInHalfOpenState(NUMBER_OF_RETRY);

    slidingWindow = new CountBasedWindow(configuration);
    context.setConfiguration(configuration);
    context.setSlidingWindow(slidingWindow);
  }

  @Test
  @DisplayName("should be REJECTED")
  void rejectedCaseTest() {
    for(int i = 0; i < SLIDING_WINDOW_SIZE; i++) {
      slidingWindow.ackAttempt(RandomUtil.generateRandomBoolean());
    }
    // late instantiation because the observer should be registered after populating slidingWindow's data
    retryManager = new OptimisticRetryManager(context);

    Assertions.assertEquals(RetryState.ON_GOING, retryManager.getRetryState());
    Assertions.assertEquals(0.0d, retryManager.getErrorRate(), 0.000001);


    for(int i = 0; i < NUMBER_OF_RETRY; i++){
      executor.execute(() -> slidingWindow.ackAttempt(RandomUtil.generateRandomBoolean()));
      if(RetryState.REJECTED.equals(retryManager.getRetryState())){
        break;
      }
    }

    Assertions.assertTrue(retryManager.getErrorRate() >= ERROR_THRESHOLD);
  }

  @Test
  @DisplayName("should be ACCEPTED, testcase: errorThreshold=80%, success ack = 21")
  void acceptedCaseTest() {
    for(int i = 0; i < SLIDING_WINDOW_SIZE; i++) {
      slidingWindow.ackAttempt(RandomUtil.generateRandomBoolean());
    }
    ERROR_THRESHOLD = 0.8;

    configuration.setErrorThreshold(ERROR_THRESHOLD);

    // late instantiation because the observer should be registered after populating slidingWindow's data
    retryManager = new OptimisticRetryManager(context);

    Assertions.assertEquals(RetryState.ON_GOING, retryManager.getRetryState());
    Assertions.assertEquals(0.0d, retryManager.getErrorRate(), 0.000001);

    int minSuccessAck = (int) (((1.0d - ERROR_THRESHOLD) * NUMBER_OF_RETRY)) + 2;

    List<Future<?>> futureList = new LinkedList<>();
    for(int i = 0; i < minSuccessAck && retryManager.acquireRetryPermission(); i++){
      futureList.add(executor.submit(() -> slidingWindow.ackAttempt(true)));
    }
    for(int i = 0; i < NUMBER_OF_RETRY - minSuccessAck; i++){
      futureList.add(executor.submit(() -> slidingWindow.ackAttempt(false)));
    }

    futureList.stream().forEach(FunctionalUtil.doNothingConsumer());

    Assertions.assertTrue(retryManager.getErrorRate() < ERROR_THRESHOLD);

    // assert if the quota has been exceeded
    Assertions.assertFalse(retryManager.acquireRetryPermission());
    Assertions.assertEquals(RetryState.ACCEPTED, retryManager.getRetryState());
  }
}
