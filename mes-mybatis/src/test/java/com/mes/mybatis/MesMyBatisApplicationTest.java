package com.mes.mybatis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 應用程式啟動測試。
 * 驗證 Spring 容器能正常載入所有 Bean。
 */
@SpringBootTest
@DisplayName("MesMyBatisApplication 啟動測試")
class MesMyBatisApplicationTest {

    @Test
    @DisplayName("應用程式上下文應能正常載入")
    void contextLoads() {
        // Spring 容器啟動成功即表示所有 Bean 配置正確
    }
}
