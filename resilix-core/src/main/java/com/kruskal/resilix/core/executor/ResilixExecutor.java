package com.kruskal.resilix.core.executor;

import com.kruskal.resilix.core.ExecResult;

import java.util.function.Supplier;

/**
 * {@link ResilixExecutor} is an interface that provides execution methods for functional interface
 * that have or doesn't have <b>throws</b> method signature.
 * */
public interface ResilixExecutor extends CheckedExecutor {
  boolean execute(Runnable runnable);
  <T> ExecResult<T> execute(Supplier<T> supplier);
}
