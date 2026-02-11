package com.mes.common.ddd.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * [DDD Pattern: Value Object 標記]
 *
 * 標記一個類別為 Value Object。
 * Value Object 的特性：
 * 1. 不可變 (Immutable)
 * 2. 沒有唯一識別 (No Identity)
 * 3. 相等性由所有屬性值決定 (Equality by Value)
 * 4. 可自由替換 (Replaceable)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ValueObject {
}
