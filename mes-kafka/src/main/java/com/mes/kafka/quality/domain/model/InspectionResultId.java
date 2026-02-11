package com.mes.kafka.quality.domain.model;

import com.mes.common.ddd.annotation.ValueObject;
import com.mes.common.ddd.model.Identity;

/**
 * [DDD Pattern: Value Object - Identity]
 * [SOLID: SRP - 只負責檢驗結果的唯一識別]
 *
 * 檢驗結果的強型別識別碼。
 * 每筆檢驗結果（InspectionResult）都有獨立的 ID，
 * 作為聚合內部 Entity 的識別。
 */
@ValueObject
public class InspectionResultId extends Identity<String> {

    public InspectionResultId(String value) {
        super(value);
    }
}
