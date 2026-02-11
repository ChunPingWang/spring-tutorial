package com.mes.kafka.quality.domain.model;

import com.mes.common.ddd.annotation.ValueObject;
import com.mes.common.ddd.model.BaseValueObject;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * [DDD Pattern: Value Object - 品質標準]
 * [SOLID: SRP - 只負責定義單一品質指標的允收範圍]
 *
 * 品質標準值物件，定義量測值的允收上下限。
 * 用於判定檢驗結果是否合格。
 *
 * <p>範例：</p>
 * <pre>
 * QualityStandard standard = new QualityStandard("DIM-001", 9.95, 10.05, "mm");
 * // 表示尺寸 DIM-001 的允收範圍為 9.95mm ~ 10.05mm
 * </pre>
 */
@ValueObject
public class QualityStandard extends BaseValueObject {

    private final String standardCode;
    private final double lowerBound;
    private final double upperBound;
    private final String unit;

    public QualityStandard(String standardCode, double lowerBound, double upperBound, String unit) {
        this.standardCode = Objects.requireNonNull(standardCode, "Standard code must not be null");
        if (lowerBound > upperBound) {
            throw new IllegalArgumentException("Lower bound must not exceed upper bound");
        }
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.unit = Objects.requireNonNull(unit, "Unit must not be null");
    }

    public String getStandardCode() {
        return standardCode;
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public double getUpperBound() {
        return upperBound;
    }

    public String getUnit() {
        return unit;
    }

    /**
     * 計算標準的中心值（平均值）。
     *
     * @return 上下限的平均值
     */
    public double getMean() {
        return (lowerBound + upperBound) / 2.0;
    }

    @Override
    protected List<Object> getEqualityComponents() {
        return Arrays.<Object>asList(standardCode, lowerBound, upperBound, unit);
    }
}
