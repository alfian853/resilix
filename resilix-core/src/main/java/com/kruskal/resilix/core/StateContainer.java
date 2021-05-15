package com.kruskal.resilix.core;

import com.kruskal.resilix.core.state.StateHandler;

/**
 * A {@link StateContainer} provides setter and getter for {@link StateHandler} instance.
 */
public interface StateContainer {
  void setStateHandler(StateHandler newStateHandler);
  StateHandler getStateHandler();
}
