package com.mes.boot.workorder.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * [Spring Boot: @ConfigurationProperties 型別安全的組態綁定]
 * [SOLID: SRP - 只負責工單模組的外部化組態]
 *
 * 工單模組的組態屬性類別。
 * 透過 Spring Boot 的 {@link ConfigurationProperties} 機制，
 * 將 application.yml 中 mes.workorder 前綴的屬性自動綁定到此類別。
 *
 * 組態範例（application.yml）：
 * <pre>
 * mes:
 *   workorder:
 *     default-priority: MEDIUM
 *     max-active-orders: 100
 * </pre>
 */
@ConfigurationProperties(prefix = "mes.workorder")
public class WorkOrderProperties {

    /**
     * 預設的工單優先順序（當未指定時使用）。
     * 可選值：LOW, MEDIUM, HIGH, URGENT
     */
    private String defaultPriority = "MEDIUM";

    /**
     * 最大活躍工單數量限制。
     * 超過此數量時將拒絕建立新的工單。
     */
    private int maxActiveOrders = 100;

    public String getDefaultPriority() {
        return defaultPriority;
    }

    public void setDefaultPriority(String defaultPriority) {
        this.defaultPriority = defaultPriority;
    }

    public int getMaxActiveOrders() {
        return maxActiveOrders;
    }

    public void setMaxActiveOrders(int maxActiveOrders) {
        this.maxActiveOrders = maxActiveOrders;
    }

    @Override
    public String toString() {
        return "WorkOrderProperties{" +
                "defaultPriority='" + defaultPriority + '\'' +
                ", maxActiveOrders=" + maxActiveOrders +
                '}';
    }
}
