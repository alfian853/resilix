package com.kruskal.resilix.core.test;

import com.kruskal.resilix.core.*;
import com.kruskal.resilix.core.state.StateHandler;
import com.kruskal.resilix.core.test.testutil.CustomTestException;
import com.kruskal.resilix.core.test.testutil.FunctionalUtil;
import com.kruskal.resilix.core.util.CheckedRunnable;
import com.kruskal.resilix.core.util.CheckedSupplier;
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
  void init() throws Throwable {
    when(stateHandlerTrue.executeChecked(any(CheckedRunnable.class))).thenReturn(true);
    when(stateHandlerTrue.executeChecked(any(CheckedSupplier.class))).thenReturn(ResultWrapper.executionResult(true));
    when(stateHandlerFalse.executeChecked(any(CheckedRunnable.class))).thenThrow(new CustomTestException());
    when(stateHandlerFalse.executeChecked(any(CheckedSupplier.class))).thenThrow(new CustomTestException());
  }

  @Test
  void allCheckedStateHandlerMethodCallTest() throws Throwable {

    Context context = new Context();
    context.setConfiguration(new Configuration());
    context.setSlidingWindow(mock(SlidingWindow.class));
    ResilixProxy resilixProxy = new ResilixProxy(context);
    resilixProxy.setStateHandler(stateHandlerTrue);

    Assertions.assertTrue(resilixProxy.executeChecked(FunctionalUtil.doNothingCheckedRunnable()));
    Assertions.assertTrue(resilixProxy.executeChecked(FunctionalUtil.trueCheckedSupplier()).isExecuted());
    Assertions.assertSame(stateHandlerTrue, resilixProxy.getStateHandler());

    verify(stateHandlerTrue, times(3)).evaluateState();
    verify(stateHandlerTrue).executeChecked(any(CheckedRunnable.class));
    verify(stateHandlerTrue).executeChecked(any(CheckedSupplier.class));

    doAnswer(invocationOnMock -> {
      resilixProxy.setStateHandler(stateHandlerFalse);
      return invocationOnMock;
    }).when(stateHandlerTrue).evaluateState();
    Assertions.assertNotSame(stateHandlerTrue, resilixProxy.getStateHandler());

    Assertions.assertThrows(CustomTestException.class,
        () -> resilixProxy.executeChecked(FunctionalUtil.trueCheckedSupplier())
    );
    Assertions.assertSame(stateHandlerFalse, resilixProxy.getStateHandler());

    verify(stateHandlerFalse).executeChecked(any(CheckedSupplier.class));
    verify(stateHandlerFalse, times(2)).evaluateState();
  }

  @Test
  void allStateHandlerMethodCallTest() throws Throwable {

    Context context = new Context();
    context.setConfiguration(new Configuration());
    context.setSlidingWindow(mock(SlidingWindow.class));
    ResilixProxy resilixProxy = new ResilixProxy(context);
    resilixProxy.setStateHandler(stateHandlerTrue);

    Assertions.assertTrue(resilixProxy.execute(FunctionalUtil.doNothingRunnable()));
    Assertions.assertTrue(resilixProxy.execute(FunctionalUtil.trueSupplier()).isExecuted());
    Assertions.assertSame(stateHandlerTrue, resilixProxy.getStateHandler());

    verify(stateHandlerTrue, times(3)).evaluateState();
    verify(stateHandlerTrue).executeChecked(any(CheckedRunnable.class));

    doAnswer(invocationOnMock -> {
      resilixProxy.setStateHandler(stateHandlerFalse);
      return invocationOnMock;
    }).when(stateHandlerTrue).evaluateState();
    Assertions.assertNotSame(stateHandlerTrue, resilixProxy.getStateHandler());

    Assertions.assertThrows(CustomTestException.class,
        () -> resilixProxy.execute(FunctionalUtil.trueSupplier())
    );
    Assertions.assertThrows(CustomTestException.class,
        () -> resilixProxy.execute(FunctionalUtil.throwErrorRunnable())
    );

    Assertions.assertSame(stateHandlerFalse, resilixProxy.getStateHandler());

    verify(stateHandlerFalse).executeChecked(any(CheckedSupplier.class));
    verify(stateHandlerFalse, times(3)).evaluateState();
  }




}
