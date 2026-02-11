package com.mes.common.ddd.model;

import java.util.Arrays;
import java.util.List;

/**
 * [DDD Pattern: Value Object 基礎類別]
 * [SOLID: LSP - 所有子類別都可替換使用此基礎類別]
 *
 * Value Object 的特性：
 * 1. 不可變 (Immutable) — 建構後不可修改
 * 2. 相等性由值決定 (Equality by Value) — 所有屬性相同即為相等
 * 3. 無副作用 (Side-Effect Free) — 操作只回傳新物件
 *
 * 子類別必須實作 {@link #getEqualityComponents()} 回傳用於比較的屬性。
 */
public abstract class BaseValueObject {

    /**
     * 回傳用於判斷相等性的屬性列表。
     * 子類別必須實作此方法，列出所有參與 equals/hashCode 計算的欄位。
     *
     * 範例：
     * <pre>
     * protected List<Object> getEqualityComponents() {
     *     return Arrays.asList(productCode, productName);
     * }
     * </pre>
     */
    protected abstract List<Object> getEqualityComponents();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseValueObject that = (BaseValueObject) o;
        return getEqualityComponents().equals(that.getEqualityComponents());
    }

    @Override
    public int hashCode() {
        return getEqualityComponents().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + getEqualityComponents();
    }
}
