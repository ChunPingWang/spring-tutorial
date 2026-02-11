package com.mes.boot.workorder.domain.model;

/**
 * [DDD Pattern: Value Object - Enumeration]
 * [SOLID: SRP - 只負責定義工單的優先順序等級]
 *
 * 工單優先順序枚舉。
 * 在 MES 系統中，優先順序影響排程策略與資源分配。
 *
 * 等級由低到高：LOW → MEDIUM → HIGH → URGENT
 */
public enum Priority {

    /** 低優先 — 可延後處理 */
    LOW("低", 1),

    /** 中優先 — 正常排程 */
    MEDIUM("中", 2),

    /** 高優先 — 優先安排 */
    HIGH("高", 3),

    /** 緊急 — 立即安排 */
    URGENT("緊急", 4);

    private final String displayName;
    private final int level;

    Priority(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLevel() {
        return level;
    }

    /**
     * 判斷此優先順序是否為高優先（HIGH 或 URGENT）。
     *
     * @return 若為 HIGH 或 URGENT 回傳 true
     */
    public boolean isHighPriority() {
        return this == HIGH || this == URGENT;
    }
}
