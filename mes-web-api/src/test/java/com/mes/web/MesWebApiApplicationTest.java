package com.mes.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * [Spring Boot: 應用程式上下文載入測試]
 *
 * 驗證 Spring 應用程式上下文可以正確載入，
 * 所有 Bean 定義和依賴注入都正確配置。
 */
@DisplayName("MesWebApiApplication 應用程式啟動測試")
@SpringBootTest
class MesWebApiApplicationTest {

    @Test
    @DisplayName("應用程式上下文應成功載入")
    void contextLoads() {
        // 如果 Spring 上下文載入失敗，此測試會自動失敗
    }
}
