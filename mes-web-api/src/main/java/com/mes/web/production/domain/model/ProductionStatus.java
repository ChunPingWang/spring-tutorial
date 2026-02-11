package com.mes.web.production.domain.model;

/**
 * [DDD Pattern: Value Object - Enum]
 * [SOLID: OCP - 新增狀態不影響既有狀態的行為]
 *
 * 生產紀錄的狀態列舉，代表生產流程的各個階段。
 *
 * 狀態轉換規則：
 * <pre>
 *   PENDING --start()--> RUNNING --pause()--> PAUSED
 *                          |                    |
 *                          |                    +--resume()--> RUNNING
 *                          |
 *                          +--finish()--> FINISHED
 * </pre>
 */
public enum ProductionStatus {

    /** 待開始 - 生產紀錄已建立但尚未啟動 */
    PENDING("待開始"),

    /** 進行中 - 生產正在執行 */
    RUNNING("進行中"),

    /** 已暫停 - 生產因故暫停 */
    PAUSED("已暫停"),

    /** 已完成 - 生產已結束 */
    FINISHED("已完成");

    private final String description;

    ProductionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
