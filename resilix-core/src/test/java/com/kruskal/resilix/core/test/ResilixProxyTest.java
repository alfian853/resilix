package com.kruskal.resilix.core.test;

import com.kruskal.resilix.core.Configuration;
import com.kruskal.resilix.core.Context;
import com.kruskal.resilix.core.ExecutionDeniedException;
import com.kruskal.resilix.core.ResilixProxy;
import com.kruskal.resilix.core.state.StateHandler;
import com.kruskal.resilix.core.test.testutil.FunctionalUtil;
import com.kruskal.resilix.core.window.SlidingWindow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ResilixProxyTest {

  StateHandler stateHandlerTrue = mock(StateHandler.class);
  StateHandler stateHandlerFalse = mock(StateHandler.class);

  @BeforeEach
  void init() throws ExecutionDeniedException {
    when(stateHandlerTrue.acquirePermission()).thenReturn(true);
    when(stateHandlerFalse.acquirePermission()).thenReturn(false);
    when(stateHandlerFalse.execute(any(Supplier.class))).thenThrow(new ExecutionDeniedException());
  }

  @Test
  void getStateHandlerTest() throws ExecutionDeniedException {

    Context context = new Context();
    context.setConfiguration(new Configuration());
    context.setSlidingWindow(mock(SlidingWindow.class));
    ResilixProxy resilixProxy = new ResilixProxy(context);
    resilixProxy.setStateHandler(stateHandlerTrue);

    Assertions.assertTrue(resilixProxy.acquirePermission());
    resilixProxy.execute(FunctionalUtil.doNothingRunnable());
    Assertions.assertSame(stateHandlerTrue, resilixProxy.getStateHandler());

    verify(stateHandlerTrue, times(3)).evaluateState();
    verify(stateHandlerTrue).acquirePermission();
    verify(stateHandlerTrue).execute(any(Runnable.class));

    doAnswer(invocationOnMock -> {
      resilixProxy.setStateHandler(stateHandlerFalse);
      return false;
    }).when(stateHandlerTrue).evaluateState();
    Assertions.assertNotSame(stateHandlerTrue, resilixProxy.getStateHandler());

    Assertions.assertFalse(resilixProxy.acquirePermission());
    Assertions.assertThrows(ExecutionDeniedException.class,
        () -> resilixProxy.execute(FunctionalUtil.doNothingRunnable())
    );
    Assertions.assertSame(stateHandlerFalse, resilixProxy.getStateHandler());

    verify(stateHandlerFalse).acquirePermission();
    verify(stateHandlerFalse).execute(any(Runnable.class));
    verify(stateHandlerFalse, times(3)).evaluateState();
  }
}
