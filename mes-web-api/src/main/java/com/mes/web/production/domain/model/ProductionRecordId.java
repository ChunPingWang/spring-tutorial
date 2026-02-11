package com.mes.web.production.domain.model;

import com.mes.common.ddd.annotation.ValueObject;
import com.mes.common.ddd.model.Identity;

import java.util.UUID;

/**
 * [DDD Pattern: Value Object - Identity]
 * [SOLID: SRP - 只負責生產紀錄識別碼的封裝與產生]
 *
 * 將原始的 String ID 包裝為強型別的 ProductionRecordId，
 * 避免與其他聚合的 ID 混淆（如 WorkOrderId、ProductionLineId）。
 *
 * 使用 UUID 作為預設的 ID 產生策略。
 */
@ValueObject
public final class ProductionRecordId extends Identity<String> {

    public ProductionRecordId(String value) {
        super(value);
    }

    /**
     * 工廠方法：產生新的隨機 ProductionRecordId。
     *
     * @return 新的 ProductionRecordId 實例
     */
    public static ProductionRecordId generate() {
        return new ProductionRecordId(UUID.randomUUID().toString());
    }

    /**
     * 工廠方法：從既有的字串值建立 ProductionRecordId。
     *
     * @param value 既有的 ID 字串
     * @return ProductionRecordId 實例
     */
    public static ProductionRecordId of(String value) {
        return new ProductionRecordId(value);
    }
}
