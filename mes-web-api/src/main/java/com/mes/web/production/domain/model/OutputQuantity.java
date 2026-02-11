package com.mes.web.production.domain.model;

import com.mes.common.ddd.annotation.ValueObject;
import com.mes.common.ddd.model.BaseValueObject;
import com.mes.common.exception.DomainException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

/**
 * [DDD Pattern: Value Object - 自驗證]
 * [SOLID: SRP - 只負責產量數據的封裝與計算]
 *
 * 生產產出數量的值物件，包含良品數、不良品數、重工品數。
 * 自驗證 (Self-Validating)：在建構時即驗證所有業務規則。
 *
 * 業務規則：
 * - 所有數量不可為負數
 * - 不良品數 + 重工品數不可超過總產出
 */
@ValueObject
public final class OutputQuantity extends BaseValueObject {

    private final int good;
    private final int defective;
    private final int rework;

    public OutputQuantity(int good, int defective, int rework) {
        validate(good, defective, rework);
        this.good = good;
        this.defective = defective;
        this.rework = rework;
    }

    private void validate(int good, int defective, int rework) {
        if (good < 0) {
            throw new DomainException("良品數量不可為負數: " + good);
        }
        if (defective < 0) {
            throw new DomainException("不良品數量不可為負數: " + defective);
        }
        if (rework < 0) {
            throw new DomainException("重工品數量不可為負數: " + rework);
        }
    }

    /**
     * 取得總產出數量。
     *
     * @return good + defective + rework
     */
    public int getTotal() {
        return good + defective + rework;
    }

    /**
     * 計算良率 (Yield Rate)。
     * 良率 = 良品數 / 總產出 * 100
     *
     * @return 良率百分比（0 ~ 100），若總產出為 0 則回傳 0
     */
    public BigDecimal getYieldRate() {
        int total = getTotal();
        if (total == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(good)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
    }

    public int getGood() {
        return good;
    }

    public int getDefective() {
        return defective;
    }

    public int getRework() {
        return rework;
    }

    /**
     * 工廠方法：建立零產出的 OutputQuantity。
     *
     * @return 良品 0、不良品 0、重工品 0
     */
    public static OutputQuantity zero() {
        return new OutputQuantity(0, 0, 0);
    }

    @Override
    protected List<Object> getEqualityComponents() {
        return Arrays.<Object>asList(good, defective, rework);
    }

    @Override
    public String toString() {
        return "OutputQuantity{good=" + good + ", defective=" + defective +
                ", rework=" + rework + ", total=" + getTotal() +
                ", yieldRate=" + getYieldRate() + "%}";
    }
}
