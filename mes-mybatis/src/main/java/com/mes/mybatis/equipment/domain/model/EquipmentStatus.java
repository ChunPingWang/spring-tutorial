package com.mes.mybatis.equipment.domain.model;

/**
 * [DDD Pattern: Value Object - 設備狀態列舉]
 *
 * 代表設備的運行狀態，狀態轉換由 Equipment 聚合根控制。
 *
 * 狀態機：
 * IDLE -> RUNNING -> IDLE
 * IDLE/RUNNING -> MAINTENANCE -> IDLE
 * IDLE/RUNNING -> BREAKDOWN -> MAINTENANCE -> IDLE
 * ANY -> DECOMMISSIONED (終態，不可逆)
 */
public enum EquipmentStatus {

    /** 閒置 */
    IDLE,

    /** 運行中 */
    RUNNING,

    /** 維護中 */
    MAINTENANCE,

    /** 故障 */
    BREAKDOWN,

    /** 已報廢 */
    DECOMMISSIONED
}
