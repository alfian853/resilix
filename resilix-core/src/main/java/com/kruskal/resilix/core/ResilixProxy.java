package com.kruskal.resilix.core;

import com.kruskal.resilix.core.state.CloseStateHandler;
import com.kruskal.resilix.core.state.StateHandler;
import com.kruskal.resilix.core.util.CheckedRunnable;
import com.kruskal.resilix.core.util.CheckedSupplier;

import java.util.function.Supplier;

public class ResilixProxy implements ResilixExecutor, StateContainer {

  private StateHandler stateHandler;

  @SuppressWarnings("unchecked")
  private static <T extends Throwable, R> void sneakyThrow(Throwable t) throws T {
    throw (T) t;
  }

  public ResilixProxy(Context context){
    stateHandler = new CloseStateHandler(context, this);
  }

  @Override
  public boolean executeChecked(CheckedRunnable runnable) throws Throwable {
    stateHandler.evaluateState();
    return stateHandler.executeChecked(runnable);
  }

  @Override
  public <T> ResultWrapper<T> executeChecked(CheckedSupplier<T> supplier) throws Throwable {
    stateHandler.evaluateState();
    return stateHandler.executeChecked(supplier);
  }

  @Override
  public boolean execute(Runnable runnable) {
    try {
      return this.executeChecked(runnable::run);
    }
    catch (Throwable throwable){
      sneakyThrow(throwable);
    }

    //this will not reached
    return false;
  }

  @Override
  public <T> ResultWrapper<T> execute(Supplier<T> supplier) {
    try{
      return this.executeChecked(supplier::get);
    }
    catch (Throwable throwable){
      sneakyThrow(throwable);
    }

    //this will not reached
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
