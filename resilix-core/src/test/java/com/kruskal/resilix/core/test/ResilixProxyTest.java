package com.kruskal.resilix.core.test;

import com.kruskal.resilix.core.*;
import com.kruskal.resilix.core.state.StateHandler;
import com.kruskal.resilix.core.test.testutil.CustomTestException;
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
  void init() {
    when(stateHandlerTrue.execute(any(Runnable.class))).thenReturn(true);
    when(stateHandlerTrue.execute(any(XSupplier.class))).thenReturn(ResultWrapper.executionResult(true));
    when(stateHandlerFalse.execute(any(XSupplier.class))).thenThrow(new CustomTestException());
  }

  @Test
  void getStateHandlerTest() {

    Context context = new Context();
    context.setConfiguration(new Configuration());
    context.setSlidingWindow(mock(SlidingWindow.class));
    ResilixProxy resilixProxy = new ResilixProxy(context);
    resilixProxy.setStateHandler(stateHandlerTrue);

    Assertions.assertTrue(resilixProxy.execute(FunctionalUtil.doNothingRunnable()));
    Assertions.assertTrue(resilixProxy.execute(FunctionalUtil.trueSupplier()).isExecuted());
    Assertions.assertSame(stateHandlerTrue, resilixProxy.getStateHandler());

    verify(stateHandlerTrue, times(3)).evaluateState();
    verify(stateHandlerTrue).execute(any(Runnable.class));

    doAnswer(invocationOnMock -> {
      resilixProxy.setStateHandler(stateHandlerFalse);
      return invocationOnMock;
    }).when(stateHandlerTrue).evaluateState();
    Assertions.assertNotSame(stateHandlerTrue, resilixProxy.getStateHandler());

    Assertions.assertThrows(CustomTestException.class,
        () -> resilixProxy.execute(FunctionalUtil.trueSupplier())
    );
    Assertions.assertSame(stateHandlerFalse, resilixProxy.getStateHandler());

    verify(stateHandlerFalse).execute(any(XSupplier.class));
    verify(stateHandlerFalse, times(2)).evaluateState();
  }
}
