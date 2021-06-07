package com.kruskal.resilix.core;

public class ExecResult<T> {
  boolean isExecuted = false;
  T result = null;

  public static <T> ExecResult<T> notExecutedResult(){
    return new ExecResult<>();
  }

  public static <T> ExecResult<T> executionResult(T result){
    ExecResult<T> execResult = new ExecResult<>();
    execResult.setExecuted(true);
    execResult.setResult(result);

    return execResult;
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
