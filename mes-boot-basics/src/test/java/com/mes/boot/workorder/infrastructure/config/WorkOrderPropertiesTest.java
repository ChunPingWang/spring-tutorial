package com.mes.boot.workorder.infrastructure.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * [Spring Boot: @ConfigurationProperties 綁定測試]
 * [SOLID: SRP - 只負責驗證組態屬性的正確綁定]
 *
 * 測試 WorkOrderProperties 是否正確從 application.yml 載入組態。
 * 使用 dev profile（application-dev.yml）的組態值進行驗證。
 */
@SpringBootTest
@DisplayName("工單組態屬性綁定測試 (WorkOrderProperties)")
class WorkOrderPropertiesTest {

    @Autowired
    private WorkOrderProperties workOrderProperties;

    @Test
    @DisplayName("應成功注入 WorkOrderProperties Bean")
    void shouldInjectProperties() {
        assertThat(workOrderProperties).isNotNull();
    }

    @Test
    @DisplayName("defaultPriority 應正確綁定")
    void shouldBindDefaultPriority() {
        assertThat(workOrderProperties.getDefaultPriority()).isNotNull();
        assertThat(workOrderProperties.getDefaultPriority()).isEqualTo("MEDIUM");
    }

    @Test
    @DisplayName("maxActiveOrders 應正確綁定（dev profile 值為 50）")
    void shouldBindMaxActiveOrders() {
        assertThat(workOrderProperties.getMaxActiveOrders()).isEqualTo(50);
    }

    @Test
    @DisplayName("toString 應包含所有屬性值")
    void shouldHaveReadableToString() {
        String str = workOrderProperties.toString();

        assertThat(str).contains("defaultPriority");
        assertThat(str).contains("maxActiveOrders");
    }
}
