package com.kruskal.resilix.core.test.testutil;

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

  public static Runnable doNothingRunnable(){
    return () -> {};
  }

  public static Runnable throwErrorRunnable(){
    return () -> {throw new RuntimeException();};
  }

  public static Supplier<Boolean> trueSupplier(){
    return () -> true;
  }

  public static <T> Supplier<T> throwErrorSupplier(){
    return () -> {throw new RuntimeException();};
  }
}
