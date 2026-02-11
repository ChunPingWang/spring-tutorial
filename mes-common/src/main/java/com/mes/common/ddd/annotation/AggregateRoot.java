package com.mes.common.ddd.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * [DDD Pattern: Aggregate Root 標記]
 *
 * 標記一個類別為 Aggregate Root。
 * Aggregate Root 是 DDD 中的核心概念，負責維護聚合內所有物件的一致性。
 * 外部只能透過 Aggregate Root 來存取聚合內部的 Entity 和 Value Object。
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AggregateRoot {
}
