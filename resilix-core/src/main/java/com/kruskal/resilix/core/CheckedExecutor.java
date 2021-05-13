package com.kruskal.resilix.core;

import com.kruskal.resilix.core.util.CheckedRunnable;
import com.kruskal.resilix.core.util.CheckedSupplier;

public interface CheckedExecutor {
  boolean executeChecked(CheckedRunnable runnable) throws Throwable;
  <T> ResultWrapper<T> executeChecked(CheckedSupplier<T> supplier) throws Throwable;

}
