package com.kruskal.resilix.core;

public class ResultWrapper<T> {
  boolean isExecuted = false;
  T result = null;

  public static <T> ResultWrapper<T> notExecutedResult(){
    return new ResultWrapper<>();
  }

  public static <T> ResultWrapper<T> executionResult(T result){
    ResultWrapper<T> resultWrapper = new ResultWrapper<>();
    resultWrapper.setExecuted(true);
    resultWrapper.setResult(result);

    return resultWrapper;
  }

  public boolean isExecuted() {
    return isExecuted;
  }

  public void setExecuted(boolean executed) {
    isExecuted = executed;
  }

  public T getResult() {
    return result;
  }

  public void setResult(T result) {
    this.result = result;
  }



}
