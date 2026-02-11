package com.mes.kafka.quality.domain.model;

import com.mes.common.ddd.annotation.ValueObject;
import com.mes.common.ddd.model.BaseValueObject;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * [DDD Pattern: Value Object - 缺陷詳情]
 * [SOLID: SRP - 只負責描述單一缺陷的完整資訊]
 *
 * 缺陷詳情值物件，記錄品質檢驗中發現的缺陷。
 * 作為不可變物件，一旦建立就不能修改。
 *
 * <p>包含資訊：</p>
 * <ul>
 *   <li>defectCode - 缺陷代碼（如 DEF-001）</li>
 *   <li>defectType - 缺陷類型（如 外觀缺陷、尺寸偏差）</li>
 *   <li>severity - 嚴重程度（如 CRITICAL, MAJOR, MINOR）</li>
 *   <li>description - 缺陷描述</li>
 * </ul>
 */
@ValueObject
public class DefectDetail extends BaseValueObject {

    private final String defectCode;
    private final String defectType;
    private final String severity;
    private final String description;

    public DefectDetail(String defectCode, String defectType, String severity, String description) {
        this.defectCode = Objects.requireNonNull(defectCode, "Defect code must not be null");
        this.defectType = Objects.requireNonNull(defectType, "Defect type must not be null");
        this.severity = Objects.requireNonNull(severity, "Severity must not be null");
        this.description = description;
    }

    public String getDefectCode() {
        return defectCode;
    }

    public String getDefectType() {
        return defectType;
    }

    public String getSeverity() {
        return severity;
    }

    public String getDescription() {
        return description;
    }

    @Override
    protected List<Object> getEqualityComponents() {
        return Arrays.<Object>asList(defectCode, defectType, severity, description);
    }
}
