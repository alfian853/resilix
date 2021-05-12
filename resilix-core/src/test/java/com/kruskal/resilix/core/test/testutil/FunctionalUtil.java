package com.kruskal.resilix.core.test.testutil;

import com.kruskal.resilix.core.XSupplier;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

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
    return () -> {throw new CustomTestException();};
  }

  public static XSupplier<Boolean> trueSupplier(){
    return () -> true;
  }

  public static <T> XSupplier<T> throwErrorSupplier(){
    return () -> {throw new RuntimeException();};
  }
}
