package com.kruskal.resilix.core.test.testutil;

import com.kruskal.resilix.core.util.CheckedRunnable;
import com.kruskal.resilix.core.util.CheckedSupplier;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FunctionalUtil {


  public static Consumer<? super Future<?>> doNothingConsumer(){
    return t -> {
      try { t.get(); }
      catch (InterruptedException | ExecutionException ignored) {
      }
    };
  }

  public static CheckedRunnable doNothingCheckedRunnable(){
    return () -> {};
  }

  public static CheckedRunnable throwErrorCheckedRunnable(){
    return () -> {throw new CustomTestException();};
  }

  public static Runnable doNothingRunnable(){
    return () -> {};
  }

  public static Runnable throwErrorRunnable(){
    return () -> {throw new CustomTestException();};
  }

  public static CheckedSupplier<Boolean> trueCheckedSupplier(){
    return () -> true;
  }

  public static <T> CheckedSupplier<T> throwErrorCheckedSupplier(){
    return () -> {throw new CustomTestException();};
  }

  public static Supplier<Boolean> trueSupplier(){
    return () -> true;
  }

}
