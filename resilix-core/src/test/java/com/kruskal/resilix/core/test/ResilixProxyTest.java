package com.kruskal.resilix.core.test;

import com.kruskal.resilix.core.Configuration;
import com.kruskal.resilix.core.Context;
import com.kruskal.resilix.core.ResilixProxy;
import com.kruskal.resilix.core.state.StateHandler;
import com.kruskal.resilix.core.test.testutil.FunctionalUtil;
import com.kruskal.resilix.core.window.SlidingWindow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ResilixProxyTest {

  StateHandler stateHandlerTrue = mock(StateHandler.class);
  StateHandler stateHandlerFalse = mock(StateHandler.class);

  @BeforeEach
  void init(){
    when(stateHandlerTrue.acquirePermission()).thenReturn(true);
    when(stateHandlerTrue.execute(any())).thenReturn(true);
    when(stateHandlerFalse.acquirePermission()).thenReturn(false);
    when(stateHandlerFalse.execute(any())).thenReturn(false);
  }

  @Test
  void getStateHandlerTest(){

    Context context = new Context();
    context.setConfiguration(new Configuration());
    context.setSlidingWindow(mock(SlidingWindow.class));
    ResilixProxy resilixProxy = new ResilixProxy(context);
    resilixProxy.setStateHandler(stateHandlerTrue);

    Assertions.assertTrue(resilixProxy.acquirePermission());
    Assertions.assertTrue(resilixProxy.execute(FunctionalUtil.doNothingRunnable()));
    Assertions.assertSame(stateHandlerTrue, resilixProxy.getStateHandler());

    verify(stateHandlerTrue, times(3)).evaluateState();
    verify(stateHandlerTrue).acquirePermission();
    verify(stateHandlerTrue).execute(any());

    doAnswer(invocationOnMock -> {
      resilixProxy.setStateHandler(stateHandlerFalse);
      return false;
    }).when(stateHandlerTrue).evaluateState();
    Assertions.assertNotSame(stateHandlerTrue, resilixProxy.getStateHandler());

    Assertions.assertFalse(resilixProxy.acquirePermission());
    Assertions.assertFalse(resilixProxy.execute(FunctionalUtil.doNothingRunnable()));
    Assertions.assertSame(stateHandlerFalse, resilixProxy.getStateHandler());

    verify(stateHandlerFalse).acquirePermission();
    verify(stateHandlerFalse).execute(any());
    verify(stateHandlerFalse, times(3)).evaluateState();
  }
}
