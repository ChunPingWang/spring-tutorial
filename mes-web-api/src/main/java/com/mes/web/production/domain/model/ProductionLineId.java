package com.mes.web.production.domain.model;

import com.mes.common.ddd.annotation.ValueObject;
import com.mes.common.ddd.model.Identity;

import java.util.UUID;

/**
 * [DDD Pattern: Value Object - Identity]
 * [SOLID: SRP - 只負責產線識別碼的封裝與產生]
 *
 * 將原始的 String ID 包裝為強型別的 ProductionLineId，
 * 確保產線 ID 不會與其他聚合的 ID 混淆。
 */
@ValueObject
public final class ProductionLineId extends Identity<String> {

    public ProductionLineId(String value) {
        super(value);
    }

    /**
     * 工廠方法：產生新的隨機 ProductionLineId。
     *
     * @return 新的 ProductionLineId 實例
     */
    public static ProductionLineId generate() {
        return new ProductionLineId(UUID.randomUUID().toString());
    }

    /**
     * 工廠方法：從既有的字串值建立 ProductionLineId。
     *
     * @param value 既有的 ID 字串
     * @return ProductionLineId 實例
     */
    public static ProductionLineId of(String value) {
        return new ProductionLineId(value);
    }
}
