package com.mes.boot.workorder.domain.model;

import com.mes.common.ddd.annotation.ValueObject;
import com.mes.common.ddd.model.BaseValueObject;

import java.util.Arrays;
import java.util.List;

/**
 * [DDD Pattern: Value Object - Self-Validating]
 * [SOLID: SRP - 只負責封裝數量相關邏輯（計畫量、完成量、不良量、良率）]
 *
 * 數量值物件，封裝工單的生產數量資訊。
 * 採用自驗證（Self-Validating）模式，在建構時即驗證所有商業規則：
 * <ul>
 *   <li>所有數量不可為負數</li>
 *   <li>不良數量不可超過計畫數量</li>
 * </ul>
 *
 * 提供良率計算方法 {@link #getYieldRate()}。
 */
@ValueObject
public final class Quantity extends BaseValueObject {

    private final int planned;
    private final int completed;
    private final int defective;

    public Quantity(int planned, int completed, int defective) {
        if (planned < 0) {
            throw new IllegalArgumentException("Planned quantity must not be negative: " + planned);
        }
        if (completed < 0) {
            throw new IllegalArgumentException("Completed quantity must not be negative: " + completed);
        }
        if (defective < 0) {
            throw new IllegalArgumentException("Defective quantity must not be negative: " + defective);
        }
        if (defective > planned) {
            throw new IllegalArgumentException(
                    "Defective quantity (" + defective + ") must not exceed planned quantity (" + planned + ")");
        }

        this.planned = planned;
        this.completed = completed;
        this.defective = defective;
    }

    /**
     * 建立只有計畫量的初始數量（完成量與不良量為 0）。
     *
     * @param planned 計畫生產數量
     * @return 新的 Quantity 實例
     */
    public static Quantity ofPlanned(int planned) {
        return new Quantity(planned, 0, 0);
    }

    public int getPlanned() {
        return planned;
    }

    public int getCompleted() {
        return completed;
    }

    public int getDefective() {
        return defective;
    }

    /**
     * 計算良率（Yield Rate）。
     * 公式：(計畫量 - 不良量) / 計畫量
     *
     * @return 良率，範圍 0.0 ~ 1.0；若計畫量為 0 則回傳 0.0
     */
    public double getYieldRate() {
        if (planned == 0) {
            return 0.0;
        }
        return (planned - defective) / (double) planned;
    }

    @Override
    protected List<Object> getEqualityComponents() {
        return Arrays.asList(planned, completed, defective);
    }
}
