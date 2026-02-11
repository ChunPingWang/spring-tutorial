package com.mes.kafka.quality.domain.model;

import com.mes.common.ddd.annotation.ValueObject;
import com.mes.common.ddd.model.BaseValueObject;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * [DDD Pattern: Value Object - 量測值]
 * [SOLID: SRP - 只負責記錄一次量測的數據]
 *
 * 量測值值物件，記錄品質檢驗中的實際測量數據。
 * 包含量測值、單位、量測時間與檢驗人員資訊。
 *
 * <p>提供 {@link #isWithinStandard(QualityStandard)} 方法，
 * 可直接判斷量測值是否在品質標準的允收範圍內。</p>
 *
 * <p>範例：</p>
 * <pre>
 * MeasuredValue mv = new MeasuredValue(10.02, "mm", LocalDateTime.now(), "OP-001");
 * QualityStandard std = new QualityStandard("DIM-001", 9.95, 10.05, "mm");
 * boolean passed = mv.isWithinStandard(std); // true
 * </pre>
 */
@ValueObject
public class MeasuredValue extends BaseValueObject {

    private final double value;
    private final String unit;
    private final LocalDateTime measuredAt;
    private final String inspector;

    public MeasuredValue(double value, String unit, LocalDateTime measuredAt, String inspector) {
        this.value = value;
        this.unit = Objects.requireNonNull(unit, "Unit must not be null");
        this.measuredAt = Objects.requireNonNull(measuredAt, "Measured time must not be null");
        this.inspector = Objects.requireNonNull(inspector, "Inspector must not be null");
    }

    public double getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

    public LocalDateTime getMeasuredAt() {
        return measuredAt;
    }

    public String getInspector() {
        return inspector;
    }

    /**
     * 判斷量測值是否在品質標準的允收範圍內。
     *
     * @param standard 品質標準
     * @return true 表示量測值在允收範圍內（合格）
     */
    public boolean isWithinStandard(QualityStandard standard) {
        return value >= standard.getLowerBound() && value <= standard.getUpperBound();
    }

    @Override
    protected List<Object> getEqualityComponents() {
        return Arrays.<Object>asList(value, unit, measuredAt, inspector);
    }
}
