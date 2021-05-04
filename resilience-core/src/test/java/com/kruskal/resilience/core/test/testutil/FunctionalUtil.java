package com.kruskal.resilience.core.test.testutil;

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

}
