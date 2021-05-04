package com.kruskal.resilience.core.test.testutil;

import java.util.function.Consumer;

public class FunctionalUtil {


  public static <T> Consumer<T> doNothingConsumer(){
    return t -> { };
  }

}
