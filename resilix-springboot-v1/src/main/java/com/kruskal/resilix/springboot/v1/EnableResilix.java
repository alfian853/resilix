package com.kruskal.resilix.springboot.v1;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ResilixConfiguration.class)
public @interface EnableResilix {
}
