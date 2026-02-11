package com.mes.mybatis.equipment.domain.model;

/**
 * [DDD Pattern: Value Object - 設備類型列舉]
 *
 * 代表工廠中不同類型的設備。
 */
public enum EquipmentType {

    /** 數控機床 (Computer Numerical Control) */
    CNC,

    /** 組裝線 */
    ASSEMBLY_LINE,

    /** 工業機器人 */
    ROBOT,

    /** 輸送帶 */
    CONVEYOR,

    /** 檢測設備 */
    INSPECTION
}
