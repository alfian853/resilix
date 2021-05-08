package com.kruskal.resilience.springboot.v2;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ResilienceConfiguration.class)
public @interface EnableResilience {
}
