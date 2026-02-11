package com.mes.web.production.domain.model;

import com.mes.common.ddd.annotation.ValueObject;
import com.mes.common.ddd.model.BaseValueObject;
import com.mes.common.exception.DomainException;

import java.util.Arrays;
import java.util.List;

/**
 * [DDD Pattern: Value Object - 操作員資訊]
 * [SOLID: SRP - 只負責封裝操作員相關資訊]
 *
 * 包含操作員 ID、姓名和班次代碼。
 * 自驗證：所有欄位不可為空或空白。
 */
@ValueObject
public final class OperatorInfo extends BaseValueObject {

    private final String operatorId;
    private final String operatorName;
    private final String shiftCode;

    public OperatorInfo(String operatorId, String operatorName, String shiftCode) {
        validate(operatorId, operatorName, shiftCode);
        this.operatorId = operatorId;
        this.operatorName = operatorName;
        this.shiftCode = shiftCode;
    }

    private void validate(String operatorId, String operatorName, String shiftCode) {
        if (operatorId == null || operatorId.trim().isEmpty()) {
            throw new DomainException("操作員 ID 不可為空");
        }
        if (operatorName == null || operatorName.trim().isEmpty()) {
            throw new DomainException("操作員姓名不可為空");
        }
        if (shiftCode == null || shiftCode.trim().isEmpty()) {
            throw new DomainException("班次代碼不可為空");
        }
    }

    public String getOperatorId() {
        return operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public String getShiftCode() {
        return shiftCode;
    }

    @Override
    protected List<Object> getEqualityComponents() {
        return Arrays.<Object>asList(operatorId, operatorName, shiftCode);
    }

    @Override
    public String toString() {
        return "OperatorInfo{operatorId='" + operatorId + "', operatorName='" + operatorName +
                "', shiftCode='" + shiftCode + "'}";
    }
}
