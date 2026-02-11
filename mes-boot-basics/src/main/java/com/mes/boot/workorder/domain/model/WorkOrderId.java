package com.mes.boot.workorder.domain.model;

import com.mes.common.ddd.annotation.ValueObject;
import com.mes.common.ddd.model.Identity;

import java.util.UUID;

/**
 * [DDD Pattern: Value Object - Identity]
 * [SOLID: SRP - 只負責工單識別碼的封裝與產生]
 *
 * 將原始的 String ID 包裝為強型別的 WorkOrderId，
 * 避免與其他聚合的 ID 混淆（如 EquipmentId、MaterialId）。
 *
 * 使用 UUID 作為預設的 ID 產生策略。
 */
@ValueObject
public final class WorkOrderId extends Identity<String> {

    public WorkOrderId(String value) {
        super(value);
    }

    /**
     * 工廠方法：產生新的隨機 WorkOrderId。
     *
     * @return 新的 WorkOrderId 實例
     */
    public static WorkOrderId generate() {
        return new WorkOrderId(UUID.randomUUID().toString());
    }

    /**
     * 工廠方法：從既有的字串值建立 WorkOrderId。
     *
     * @param value 既有的 ID 字串
     * @return WorkOrderId 實例
     */
    public static WorkOrderId of(String value) {
        return new WorkOrderId(value);
    }
}
