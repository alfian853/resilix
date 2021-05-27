package com.kruskal.resilix.core.state;

import com.kruskal.resilix.core.CheckedExecutor;
import com.kruskal.resilix.core.StateContainer;

/**
 * A {@link StateHandler} handles everything related to its state such as permission management,
 * call execution and state evaluation.
 * */
public interface StateHandler extends CheckedExecutor {
  /**
   * Try to obtain permission to execute a call. this method might trigger update for certain state.
   * @return true if permitted <br>
   *         false if not permitted
   */
  boolean acquirePermission();


  /**
   * Self evaluate its state, it should trigger {@link StateContainer#setStateHandler(StateHandler)}
   * to trip to another state if necessary.
   */
  void evaluateState();
}
