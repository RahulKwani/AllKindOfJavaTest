package com.investigate.random.stuff;

import java.util.logging.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/** To measure operation execution time between multiple methods using spring AOP. */
@Aspect
@Component
public class LoggingAspect {

  private static final Logger LOGGER = Logger.getLogger(LoggingAspect.class.getName());

  @Around("execution(* com.investigate.random.stuff.BasicService.*(..))")
  public Object profileAllMethods(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();

    // Get intercepted method details
    String className = methodSignature.getDeclaringType().getSimpleName();
    String methodName = methodSignature.getName();

    final StopWatch stopWatch = new StopWatch();

    // Measure method execution time
    stopWatch.start();
    Object result = proceedingJoinPoint.proceed();
    stopWatch.stop();

    // Log method execution time
    LOGGER.info(
        "Execution time of "
            + className
            + "."
            + methodName
            + " "
            + ":: "
            + stopWatch.getTotalTimeMillis()
            + " ms");

    return result;
  }
}
