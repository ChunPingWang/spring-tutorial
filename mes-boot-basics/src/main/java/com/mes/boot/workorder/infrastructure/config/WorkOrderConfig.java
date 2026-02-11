package com.mes.boot.workorder.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * [Spring Boot: @Configuration + @EnableConfigurationProperties]
 * [SOLID: SRP - 只負責工單模組的 Spring 組態設定]
 *
 * 工單模組的 Spring 組態類別。
 * 啟用 {@link WorkOrderProperties} 的組態屬性綁定。
 *
 * 在六角架構中，組態類別屬於基礎設施層，
 * 負責將 Spring 框架的組態機制與領域/應用層連接起來。
 */
@Configuration
@EnableConfigurationProperties(WorkOrderProperties.class)
public class WorkOrderConfig {

    private static final Logger log = LoggerFactory.getLogger(WorkOrderConfig.class);

    private final WorkOrderProperties workOrderProperties;

    public WorkOrderConfig(WorkOrderProperties workOrderProperties) {
        this.workOrderProperties = workOrderProperties;
    }

    @PostConstruct
    public void init() {
        log.info("Work order module configured: {}", workOrderProperties);
    }
}
