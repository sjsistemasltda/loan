package br.company.loan.util.logger;

import br.company.loan.util.logger.annotation.AutoLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class AspectLogger {
    private final Logger logger;

    public AspectLogger(Logger logger) {
        this.logger = logger;
    }

    @Pointcut("@annotation(br.company.loan.util.logger.annotation.AutoLog)")
    public void autoLogAnnotation() {
    }

    @Pointcut("within(br.company.loan..*)")
    public void loggableMethods() {
    }

    @Pointcut("within(br.company.loan.util..*)")
    public void unloggableMethods() {
    }

    @Around("(loggableMethods() || autoLogAnnotation()) && !unloggableMethods()")
    public Object aroundLoggableMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        AutoLog autoLog = getAutoLogAnnotation(joinPoint);

        if (autoLog == null || (autoLog.active() && autoLog.logInputData())) {
            logger.forClass(joinPoint.getSignature().getDeclaringType()).info("Method started", getMethodArguments(joinPoint));
        } else {
            logger.forClass(joinPoint.getSignature().getDeclaringType()).info("Method started");
        }

        Object result = joinPoint.proceed();

        if (autoLog == null || (autoLog.active() && autoLog.logOutputData())) {
            logger.forClass(joinPoint.getSignature().getDeclaringType()).info("Method finished", result);
        } else {
            logger.forClass(joinPoint.getSignature().getDeclaringType()).info("Method finished");
        }

        return result;
    }

    @AfterThrowing(pointcut = "loggableMethods() && !unloggableMethods()", throwing = "ex")
    public void logExceptionThrown(JoinPoint joinPoint, Exception ex) {
        AutoLog autoLog = getAutoLogAnnotation(joinPoint);

        if (autoLog == null || (autoLog.active() && autoLog.logOutputData())) {
            logger.forClass(joinPoint.getSignature().getDeclaringType())
                    .error("Method threw exception", ex, getMethodArguments(joinPoint));
        } else {
            logger.forClass(joinPoint.getSignature().getDeclaringType())
                    .error("Method threw exception", ex);
        }
    }

    private AutoLog getAutoLogAnnotation(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        AutoLog annotation = AnnotationUtils.findAnnotation(method, AutoLog.class);
        if (annotation == null) {
            Class<?> type = method.getDeclaringClass();
            while (type != null && annotation == null) {
                annotation = type.getAnnotation(AutoLog.class);
                type = type.getSuperclass();
            }
        }

        return annotation;
    }

    private static Map<String, Object> getMethodArguments(JoinPoint joinPoint) {
        CodeSignature methodSignature = (CodeSignature) joinPoint.getSignature();
        String[] parameterNames = methodSignature.getParameterNames();

        Map<String, Object> parameters = new HashMap<>();
        for (int i = 0; i < parameterNames.length; i++) {
            parameters.put(parameterNames[i], joinPoint.getArgs()[i]);
        }
        return parameters;
    }
}
