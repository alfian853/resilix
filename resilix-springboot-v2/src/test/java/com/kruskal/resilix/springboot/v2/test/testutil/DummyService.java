package com.kruskal.resilix.springboot.v2.test.testutil;

import com.kruskal.resilix.springboot.v2.ResilixWatcher;
import org.springframework.stereotype.Service;

@Service
public class DummyService {

  @ResilixWatcher(contextKey = DefaultConst.contextKey)
  public Integer mockingMethod(int arg){
    return arg;
  }

  @ResilixWatcher(contextKey = DefaultConst.contextKey)
  public void tryVoid(Runnable runnable){runnable.run();}

  @ResilixWatcher(contextKey = DefaultConst.contextKey)
  public void tryVoidError(){
    throw new CustomTestRuntimeException();
  }

  @ResilixWatcher(contextKey = DefaultConst.contextKey)
  public Integer tryGetInt100ErrorRuntime(){
    throw new CustomTestRuntimeException();
  }

  @ResilixWatcher(contextKey = DefaultConst.contextKey)
  public Integer tryGetInt100Error() throws CustomTestException {
    throw new CustomTestException();
  }

}
