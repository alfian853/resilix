package com.kruskal.resilience.core.test;

import com.kruskal.resilience.core.Configuration;
import com.kruskal.resilience.core.Context;
import com.kruskal.resilience.core.ResilienceProxy;
import com.kruskal.resilience.core.state.StateHandler;
import com.kruskal.resilience.core.test.testutil.FunctionalUtil;
import com.kruskal.resilience.core.window.SlidingWindow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ResilienceProxyTest {

  StateHandler stateHandlerTrue = mock(StateHandler.class);
  StateHandler stateHandlerFalse = mock(StateHandler.class);

  @BeforeEach
  public void init(){
    when(stateHandlerTrue.acquirePermission()).thenReturn(true);
    when(stateHandlerTrue.execute(any())).thenReturn(true);
    when(stateHandlerFalse.acquirePermission()).thenReturn(false);
    when(stateHandlerFalse.execute(any())).thenReturn(false);
  }

  @Test
  public void getStateHandlerTest(){

    Context context = new Context();
    context.setConfiguration(new Configuration());
    context.setSlidingWindow(mock(SlidingWindow.class));
    ResilienceProxy resilienceProxy = new ResilienceProxy(context);
    resilienceProxy.setStateHandler(stateHandlerTrue);

    Assertions.assertTrue(resilienceProxy.acquirePermission());
    Assertions.assertTrue(resilienceProxy.execute(FunctionalUtil.doNothingRunnable()));
    Assertions.assertSame(stateHandlerTrue, resilienceProxy.getStateHandler());

    verify(stateHandlerTrue, times(3)).evaluateState();
    verify(stateHandlerTrue).acquirePermission();
    verify(stateHandlerTrue).execute(any());

    doAnswer(invocationOnMock -> {
      resilienceProxy.setStateHandler(stateHandlerFalse);
      return false;
    }).when(stateHandlerTrue).evaluateState();
    Assertions.assertNotSame(stateHandlerTrue, resilienceProxy.getStateHandler());

    Assertions.assertFalse(resilienceProxy.acquirePermission());
    Assertions.assertFalse(resilienceProxy.execute(FunctionalUtil.doNothingRunnable()));
    Assertions.assertSame(stateHandlerFalse, resilienceProxy.getStateHandler());

    verify(stateHandlerFalse).acquirePermission();
    verify(stateHandlerFalse).execute(any());
    verify(stateHandlerFalse, times(3)).evaluateState();
  }
}
