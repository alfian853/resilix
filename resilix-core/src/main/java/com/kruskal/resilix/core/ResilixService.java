package com.kruskal.resilix.core;

public interface ResilixService {
  boolean checkPermission();
  boolean execute(Runnable runnable);
  <T> ResultWrapper<T> execute(XSupplier<T> supplier);
}
