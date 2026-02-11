package com.mes.mybatis.equipment.domain.model;

import com.mes.common.ddd.annotation.ValueObject;
import com.mes.common.ddd.model.Identity;

import java.util.UUID;

/**
 * [DDD Pattern: Value Object - 設備唯一識別碼]
 *
 * 將 String 型別的 ID 包裝為強型別，避免與其他 ID 混淆。
 */
@ValueObject
public class EquipmentId extends Identity<String> {

    public EquipmentId(String value) {
        super(value);
    }

    public static EquipmentId generate() {
        return new EquipmentId(UUID.randomUUID().toString());
    }

    public static EquipmentId of(String value) {
        return new EquipmentId(value);
    }
}
