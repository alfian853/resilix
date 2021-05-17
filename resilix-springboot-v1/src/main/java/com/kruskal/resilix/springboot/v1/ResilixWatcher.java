package com.kruskal.resilix.springboot.v1;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface ResilixWatcher {
  String contextKey();
}
