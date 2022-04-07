package com.github.afezeria.freedao.spring.runtime;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Objects;

@Aspect
@Slf4j
public class FreedaoDatasourceInterceptor {
    @Pointcut("@annotation(com.github.afezeria.freedao.spring.runtime.DS)")
    public void annotatedMethod() {
    }

    @Pointcut("@within(com.github.afezeria.freedao.spring.runtime.DS)")
    public void annotatedClass() {
    }

    @Around("annotatedMethod() || annotatedClass()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DS annotation = method.getAnnotation(DS.class);
        if (annotation == null) {
            annotation = method.getClass().getAnnotation(DS.class);
        }
        if (annotation == null) {
            return joinPoint.proceed();
        }
        DS outer = DataSourceContextHolder.get();
        if (outer != null) {
            if (outer.prefix() == annotation.prefix() && Objects.equals(outer.value(), annotation.value())) {
                return joinPoint.proceed();
            }
        }
        try {
            DataSourceContextHolder.set(annotation);
            return joinPoint.proceed();
        } finally {
            DataSourceContextHolder.set(outer);
        }
    }
}