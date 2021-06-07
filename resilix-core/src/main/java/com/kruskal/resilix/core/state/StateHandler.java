package com.kruskal.resilix.core.state;

import com.kruskal.resilix.core.executor.CheckedExecutor;
import com.kruskal.resilix.core.StateContainer;

/**
 * A {@link StateHandler} handles everything related to its state such as permission management,
 * call execution and state evaluation.
 * */
public interface StateHandler extends CheckedExecutor {

  /**
   * Self evaluate its state, it should trigger {@link StateContainer#setStateHandler(StateHandler)}
   * to trip to another state if necessary.
   */
  void evaluateState();
}
