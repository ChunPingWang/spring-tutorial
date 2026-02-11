package com.mes.common.ddd.model;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * [DDD Pattern: 泛型 Identity Value Object]
 *
 * 將原始型別的 ID（如 String, Long）包裝為強型別的 Value Object，
 * 避免混淆不同聚合的 ID（例如將 WorkOrderId 誤傳給 EquipmentId）。
 *
 * @param <T> 底層 ID 值的型別
 */
public abstract class Identity<T> extends BaseValueObject {

    private final T value;

    protected Identity(T value) {
        this.value = Objects.requireNonNull(value, "Identity value must not be null");
    }

    public T getValue() {
        return value;
    }

    @Override
    protected List<Object> getEqualityComponents() {
        return Arrays.asList(value);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + value + ")";
    }
}
