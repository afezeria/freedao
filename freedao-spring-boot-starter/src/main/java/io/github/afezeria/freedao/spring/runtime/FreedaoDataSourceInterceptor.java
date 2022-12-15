package io.github.afezeria.freedao.spring.runtime;

import io.github.afezeria.freedao.classic.runtime.DS;
import io.github.afezeria.freedao.classic.runtime.SqlHelper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Aspect
@Slf4j
public class FreedaoDataSourceInterceptor {
    @Pointcut("@annotation(io.github.afezeria.freedao.classic.runtime.DS)")
    public void annotatedMethod() {
    }

    @Pointcut("@within(io.github.afezeria.freedao.classic.runtime.DS)")
    public void annotatedClass() {
    }

    @Around("annotatedMethod() || annotatedClass()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DS annotation = method.getAnnotation(DS.class);
        if (annotation == null) {
            annotation = method.getDeclaringClass().getAnnotation(DS.class);
        }
        if (annotation == null) {
            return joinPoint.proceed();
        }
        return SqlHelper.ds(annotation, () -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }
}