package com.kruskal.resilix.springboot.v1.test.testutil;

import com.kruskal.resilix.core.util.CheckedRunnable;
import junit.framework.AssertionFailedError;

public class AssertionUtil {

  public static void assertExecution(Runnable runnable, Class<?> expectedException){
    try {
      runnable.run();
    }
    catch (Throwable throwable){
      if(expectedException.isAssignableFrom(throwable.getClass())){
        return;
      }
      else{
        throw new RuntimeException("expected exception: "+expectedException +"\nthrown exception: "+throwable.getClass());
      }
    }

    throw new RuntimeException("expected exception "+ expectedException+ " isn't thrown");
  }

  public static void assertCheckedExecution(CheckedRunnable runnable, Class<?> expectedException){
    try {
      runnable.run();
    }
    catch (Throwable throwable){
      if(expectedException.isAssignableFrom(throwable.getClass())){
        return;
      }
      else{
        throw new RuntimeException("expected exception: "+expectedException +"\nthrown exception: "+throwable.getClass());
      }
    }

    throw new RuntimeException("expected exception "+ expectedException+ " isn't thrown");
  }

}
