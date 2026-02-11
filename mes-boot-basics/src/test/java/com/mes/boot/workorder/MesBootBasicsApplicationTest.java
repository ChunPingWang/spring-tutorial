package com.mes.boot.workorder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * [Spring Boot: @SpringBootTest - 應用程式上下文載入測試]
 * [SOLID: SRP - 只負責驗證 Spring Application Context 可以成功載入]
 *
 * 最基本的 Spring Boot 測試，驗證：
 * 1. 所有 Bean 定義正確且可成功建立
 * 2. 依賴注入（DI）正確配置
 * 3. @ConfigurationProperties 正確綁定
 */
@SpringBootTest
@DisplayName("應用程式上下文載入測試")
class MesBootBasicsApplicationTest {

    @Test
    @DisplayName("Spring Application Context 應成功載入")
    void contextLoads() {
        // 若 Spring Context 無法載入，此測試會自動失敗。
        // 這是最基本但也最重要的整合測試，
        // 確保所有 Bean 和組態都正確設定。
    }
}
