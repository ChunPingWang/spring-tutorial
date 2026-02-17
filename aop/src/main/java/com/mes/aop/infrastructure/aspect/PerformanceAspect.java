package com.mes.aop.infrastructure.aspect;

import com.mes.aop.application.service.LogExecutionTime;
import com.mes.aop.application.service.PerfMonitor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceAspect {

    private static final Logger log = LoggerFactory.getLogger(PerformanceAspect.class);

    @Pointcut("@annotation(com.mes.aop.application.service.PerfMonitor)")
    public void perfMonitorPointcut() {
    }

    @Pointcut("@annotation(com.mes.aop.application.service.LogExecutionTime)")
    public void logExecutionTimePointcut() {
    }

    @Around("perfMonitorPointcut()")
    public Object measurePerfMonitor(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        String methodName = pjp.getSignature().toShortString();

        log.info("[PERF] 開始執行: {}", methodName);

        try {
            Object result = pjp.proceed();
            long duration = System.currentTimeMillis() - start;
            log.info("[PERF] 完成: {}, 耗時: {}ms", methodName, duration);
            return result;
        } catch (Throwable ex) {
            long duration = System.currentTimeMillis() - start;
            log.error("[PERF] 異常: {}, 耗時: {}ms, 錯誤: {}", methodName, duration, ex.getMessage());
            throw ex;
        }
    }

    @Around("logExecutionTimePointcut()")
    public Object measureLogExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long duration = System.currentTimeMillis() - start;
        log.info("方法 {} 執行時間: {}ms", pjp.getSignature().toShortString(), duration);
        return result;
    }
}
