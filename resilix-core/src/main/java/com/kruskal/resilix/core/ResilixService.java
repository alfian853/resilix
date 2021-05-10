package com.kruskal.resilix.core;

public interface ResilixService {
  boolean acquirePermission();
  boolean execute(Runnable runnable);
}
