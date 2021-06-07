package com.kruskal.resilix.springboot.v2;

import com.kruskal.resilix.core.ResilixRegistry;
import com.kruskal.resilix.core.ExecResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Aspect
public class ResilixWatcherAspect {

  private ResilixRegistry resilixRegistry;

  public ResilixWatcherAspect(ResilixRegistry resilixRegistry) {
    this.resilixRegistry = resilixRegistry;
  }

  @Around(value = "@annotation(ResilixWatcher)")
  public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();

    ResilixWatcher myAnnotation = method.getAnnotation(ResilixWatcher.class);

    ExecResult<Object> resultWrapper = resilixRegistry.getResilixExecutor(myAnnotation.contextKey())
        .executeChecked(() -> joinPoint.proceed(joinPoint.getArgs()));

    return resultWrapper.getResult();
  }
}
