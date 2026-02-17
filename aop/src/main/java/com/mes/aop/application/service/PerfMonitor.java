package com.mes.aop.application.service;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PerfMonitor {
    String value() default "";
}
