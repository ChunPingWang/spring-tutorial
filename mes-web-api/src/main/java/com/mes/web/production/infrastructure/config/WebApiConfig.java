package com.mes.web.production.infrastructure.config;

import com.mes.web.production.domain.service.ProductionDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * [Hexagonal Architecture: Configuration - 組裝基礎設施元件]
 * [SOLID: DIP - 透過 Spring Configuration 進行依賴注入的組裝]
 *
 * Web API 模組的 Spring 配置類別。
 * 負責註冊非 @Component 標記的 Bean（如 Domain Service）。
 */
@Configuration
public class WebApiConfig {

    /**
     * 註冊 ProductionDomainService 為 Spring Bean。
     * Domain Service 使用 @DomainService 標記（非 Spring 註解），
     * 因此需要透過 @Bean 方法手動註冊。
     *
     * @return ProductionDomainService 實例
     */
    @Bean
    public ProductionDomainService productionDomainService() {
        return new ProductionDomainService();
    }
}
