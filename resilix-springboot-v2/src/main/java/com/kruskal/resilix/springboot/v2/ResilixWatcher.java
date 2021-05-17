package com.kruskal.resilix.springboot.v2;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface ResilixWatcher {
  String contextKey() default "";
}
