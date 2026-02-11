package com.mes.mybatis.equipment.domain.model;

import com.mes.common.ddd.annotation.ValueObject;
import com.mes.common.ddd.model.Identity;

import java.util.UUID;

/**
 * [DDD Pattern: Value Object - 維護記錄唯一識別碼]
 *
 * 將 String 型別的 ID 包裝為強型別，避免與其他 ID 混淆。
 */
@ValueObject
public class MaintenanceRecordId extends Identity<String> {

    public MaintenanceRecordId(String value) {
        super(value);
    }

    public static MaintenanceRecordId generate() {
        return new MaintenanceRecordId(UUID.randomUUID().toString());
    }

    public static MaintenanceRecordId of(String value) {
        return new MaintenanceRecordId(value);
    }
}
