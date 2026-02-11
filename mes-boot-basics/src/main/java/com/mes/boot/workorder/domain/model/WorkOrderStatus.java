package com.mes.boot.workorder.domain.model;

/**
 * [DDD Pattern: Value Object - Enumeration]
 * [SOLID: OCP - 新增狀態時只需擴展枚舉值，不需修改既有邏輯]
 *
 * 工單狀態枚舉，表達工單在其生命週期中的所有可能狀態。
 *
 * 狀態轉換規則：
 * <pre>
 *   CREATED ──→ IN_PROGRESS ──→ COMPLETED
 *     │              │
 *     └──→ CANCELLED ←──┘
 * </pre>
 *
 * 注意：COMPLETED 狀態為終態，不可再進行任何狀態轉換。
 */
public enum WorkOrderStatus {

    /** 已建立 — 工單剛被建立，尚未開始生產 */
    CREATED("已建立"),

    /** 進行中 — 工單已開始生產 */
    IN_PROGRESS("進行中"),

    /** 已完成 — 工單生產完畢 */
    COMPLETED("已完成"),

    /** 已取消 — 工單被取消 */
    CANCELLED("已取消");

    private final String displayName;

    WorkOrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
