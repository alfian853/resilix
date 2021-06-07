package com.kruskal.resilix.core;

import com.kruskal.resilix.core.executor.ResilixExecutor;
import com.kruskal.resilix.core.state.CloseStateHandler;
import com.kruskal.resilix.core.state.StateHandler;
import com.kruskal.resilix.core.util.CheckedRunnable;
import com.kruskal.resilix.core.util.CheckedSupplier;

import java.util.function.Supplier;

/**
 * {@link ResilixProxy} as a proxy exposes {@link StateHandler}'s methods and also trigger
 * {@link StateHandler#evaluateState()} to obtain the latest state, so it should be called before
 * any call to {@link StateHandler}, and it may trigger state change via {@link StateContainer#setStateHandler(StateHandler)}
 * method.
 * */
public class ResilixProxy implements ResilixExecutor, StateContainer {

  private StateHandler stateHandler;

  @SuppressWarnings("unchecked")
  private static <T extends Throwable> void sneakyThrow(Throwable t) throws T {
    throw (T) t;
  }

  public ResilixProxy(Context context){
    stateHandler = new CloseStateHandler(context, this);
  }

  @Override
  public boolean executeChecked(CheckedRunnable checkedRunnable) throws Throwable {
    stateHandler.evaluateState();
    return stateHandler.executeChecked(checkedRunnable);
  }

  @Override
  public <T> ExecResult<T> executeChecked(CheckedSupplier<T> checkedSupplier) throws Throwable {
    stateHandler.evaluateState();
    return stateHandler.executeChecked(checkedSupplier);
  }

  @Override
  public boolean execute(Runnable runnable) {
    try {
      return this.executeChecked(runnable::run);
    }
    catch (Throwable throwable){
      sneakyThrow(throwable);
    }

    //this will not be reached
    return false;
  }

  @Override
  public <T> ExecResult<T> execute(Supplier<T> supplier) {
    try{
      return this.executeChecked(supplier::get);
    }
    catch (Throwable throwable){
      sneakyThrow(throwable);
    }

    //this will not be reached
    return null;
  }

  @Override
  public void setStateHandler(StateHandler newStateHandler) {
    stateHandler = newStateHandler;
  }

  @Override
  public StateHandler getStateHandler() {
    stateHandler.evaluateState();
    return stateHandler;
  }

}
