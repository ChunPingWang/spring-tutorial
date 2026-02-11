package com.mes.common.ddd.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * [DDD Pattern: Domain Service 標記]
 *
 * 標記一個類別為 Domain Service。
 * Domain Service 封裝不屬於任何單一 Entity 或 Value Object 的領域邏輯，
 * 通常涉及多個聚合之間的操作或計算。
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DomainService {
}
