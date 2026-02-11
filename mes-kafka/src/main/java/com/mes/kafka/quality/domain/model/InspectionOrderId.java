package com.mes.kafka.quality.domain.model;

import com.mes.common.ddd.annotation.ValueObject;
import com.mes.common.ddd.model.Identity;

/**
 * [DDD Pattern: Value Object - Identity]
 * [SOLID: SRP - 只負責檢驗工單的唯一識別]
 *
 * 檢驗工單的強型別識別碼。
 * 使用 Identity&lt;String&gt; 包裝原始 String，
 * 避免與其他 ID（如 WorkOrderId）混淆。
 */
@ValueObject
public class InspectionOrderId extends Identity<String> {

    public InspectionOrderId(String value) {
        super(value);
    }
}
