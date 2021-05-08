package com.kruskal.resilience.core.test.retry;

import com.kruskal.resilience.core.Configuration;
import com.kruskal.resilience.core.Context;
import com.kruskal.resilience.core.constant.RetryState;
import com.kruskal.resilience.core.constant.RetryStrategy;
import com.kruskal.resilience.core.constant.SlidingWindowStrategy;
import com.kruskal.resilience.core.retry.PessimisticRetryManager;
import com.kruskal.resilience.core.test.testutil.FunctionalUtil;
import com.kruskal.resilience.core.test.testutil.RandomUtil;
import com.kruskal.resilience.core.window.CountBasedWindow;
import com.kruskal.resilience.core.window.SlidingWindow;
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

public class PesimisticRetryManagerTest {

  private int SLIDING_WINDOW_SIZE = 50;
  private double ERROR_THRESHOLD = 0.3;
  private int NUMBER_OF_RETRY = 100;

  private ExecutorService executor = Executors.newFixedThreadPool(16);

  private Configuration configuration;
  private Context context;
  private SlidingWindow slidingWindow;
  private PessimisticRetryManager retryManager;

  @BeforeEach
  public void init(){
    context = new Context();
    configuration = new Configuration();
    configuration.setRetryStrategy(RetryStrategy.PESSIMISTIC);
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
  public void rejectedCaseTest() {
    Assertions.assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {

      for(int i = 0; i < SLIDING_WINDOW_SIZE; i++) {
        slidingWindow.ackAttempt(RandomUtil.generateRandomBoolean());
      }
      // late instantiation because the observer should be registered after populating slidingWindow's data
      retryManager = new PessimisticRetryManager(context);

      Assertions.assertEquals(RetryState.ON_GOING, retryManager.getRetryState());
      Assertions.assertEquals(0.0d, retryManager.getErrorRate(), 0.000001);

      int retryCount = 0;
      while (retryManager.getErrorRate() < ERROR_THRESHOLD){
        if(retryManager.acquireRetryPermission()){
          executor.execute(() -> {
            retryManager.onBeforeRetry();
            slidingWindow.ackAttempt(RandomUtil.generateRandomBoolean());
          });
          retryCount++;
        }
      }

      Assertions.assertTrue(retryManager.getErrorRate() >= ERROR_THRESHOLD);
      Assertions.assertTrue(NUMBER_OF_RETRY > retryCount);
    });

  }

  @Test
  @DisplayName("should be ACCEPTED, testcase: errorThreshold=80%, success ack = 21")
  public void acceptedCaseTest() {
    for(int i = 0; i < SLIDING_WINDOW_SIZE; i++) {
      slidingWindow.ackAttempt(RandomUtil.generateRandomBoolean());
    }
    ERROR_THRESHOLD = 0.8;

    configuration.setErrorThreshold(ERROR_THRESHOLD);

    // late instantiation because the observer should be registered after populating slidingWindow's data
    retryManager = new PessimisticRetryManager(context);

    Assertions.assertEquals(RetryState.ON_GOING, retryManager.getRetryState());
    Assertions.assertEquals(0.0d, retryManager.getErrorRate(), 0.000001);

    int minSuccessAck = (int) (((1.0d - ERROR_THRESHOLD) * NUMBER_OF_RETRY)) + 2;

    List<Future<?>> futureList = new LinkedList<>();
    for(int i = 0; i < minSuccessAck && retryManager.getErrorRate() < ERROR_THRESHOLD; i++){
      if(retryManager.acquireRetryPermission()) {
        futureList.add(executor.submit(() -> slidingWindow.ackAttempt(true)));
      }
      else { i--; }
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
