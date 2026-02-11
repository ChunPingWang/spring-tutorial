package com.mes.web.production.domain.model;

import com.mes.common.ddd.annotation.ValueObject;
import com.mes.common.ddd.model.BaseValueObject;
import com.mes.common.exception.DomainException;

import java.util.Arrays;
import java.util.List;

/**
 * [DDD Pattern: Value Object - 製程步驟]
 * [SOLID: SRP - 只負責封裝單一製程步驟的資訊]
 *
 * 描述生產流程中的一個步驟，包含步驟編號、名稱和預計持續時間。
 * 自驗證：步驟編號必須為正整數，名稱不可為空，持續時間必須為正整數。
 */
@ValueObject
public final class ProcessStep extends BaseValueObject {

    private final int stepNumber;
    private final String stepName;
    private final int durationMinutes;

    public ProcessStep(int stepNumber, String stepName, int durationMinutes) {
        validate(stepNumber, stepName, durationMinutes);
        this.stepNumber = stepNumber;
        this.stepName = stepName;
        this.durationMinutes = durationMinutes;
    }

    private void validate(int stepNumber, String stepName, int durationMinutes) {
        if (stepNumber <= 0) {
            throw new DomainException("步驟編號必須為正整數: " + stepNumber);
        }
        if (stepName == null || stepName.trim().isEmpty()) {
            throw new DomainException("步驟名稱不可為空");
        }
        if (durationMinutes <= 0) {
            throw new DomainException("持續時間必須為正整數: " + durationMinutes);
        }
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public String getStepName() {
        return stepName;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    @Override
    protected List<Object> getEqualityComponents() {
        return Arrays.<Object>asList(stepNumber, stepName, durationMinutes);
    }

    @Override
    public String toString() {
        return "ProcessStep{stepNumber=" + stepNumber + ", stepName='" + stepName +
                "', durationMinutes=" + durationMinutes + "}";
    }
}
