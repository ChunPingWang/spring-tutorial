package com.mes.boot.workorder.domain.model;

import com.mes.common.ddd.annotation.ValueObject;
import com.mes.common.ddd.model.BaseValueObject;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * [DDD Pattern: Value Object - Self-Validating]
 * [SOLID: SRP - 只負責封裝日期範圍邏輯（起始、結束、重疊判斷）]
 *
 * 日期範圍值物件，封裝工單的計畫開始與結束日期。
 * 採用自驗證模式，確保開始日期不會晚於結束日期。
 *
 * 提供日期範圍重疊判斷方法 {@link #overlapsWith(DateRange)}，
 * 用於排程衝突檢查。
 */
@ValueObject
public final class DateRange extends BaseValueObject {

    private final LocalDate plannedStart;
    private final LocalDate plannedEnd;

    public DateRange(LocalDate plannedStart, LocalDate plannedEnd) {
        Objects.requireNonNull(plannedStart, "Planned start date must not be null");
        Objects.requireNonNull(plannedEnd, "Planned end date must not be null");

        if (plannedStart.isAfter(plannedEnd)) {
            throw new IllegalArgumentException(
                    "Planned start date (" + plannedStart + ") must not be after planned end date (" + plannedEnd + ")");
        }

        this.plannedStart = plannedStart;
        this.plannedEnd = plannedEnd;
    }

    public LocalDate getPlannedStart() {
        return plannedStart;
    }

    public LocalDate getPlannedEnd() {
        return plannedEnd;
    }

    /**
     * 判斷此日期範圍是否與另一個日期範圍重疊。
     *
     * @param other 另一個日期範圍
     * @return 若重疊回傳 true
     */
    public boolean overlapsWith(DateRange other) {
        return !this.plannedEnd.isBefore(other.plannedStart)
                && !other.plannedEnd.isBefore(this.plannedStart);
    }

    /**
     * 判斷指定日期是否在此範圍內（含邊界）。
     *
     * @param date 要檢查的日期
     * @return 若在範圍內回傳 true
     */
    public boolean contains(LocalDate date) {
        return !date.isBefore(plannedStart) && !date.isAfter(plannedEnd);
    }

    @Override
    protected List<Object> getEqualityComponents() {
        return Arrays.asList(plannedStart, plannedEnd);
    }
}
