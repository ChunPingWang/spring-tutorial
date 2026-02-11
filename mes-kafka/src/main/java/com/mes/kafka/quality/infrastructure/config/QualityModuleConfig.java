package com.mes.kafka.quality.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * [Infrastructure: 品質模組設定]
 * [SOLID: SRP - 只負責品質模組的基礎設施配置]
 *
 * 品質檢驗模組的 Spring 設定類別。
 * 配置模組所需的共用基礎設施 Bean。
 */
@Configuration
public class QualityModuleConfig {

    private static final Logger log = LoggerFactory.getLogger(QualityModuleConfig.class);

    /**
     * 配置 Jackson ObjectMapper，支援 Java 8 日期時間序列化。
     * 用於領域事件的 JSON 序列化與反序列化。
     *
     * @return 已配置的 ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        log.info("Configured ObjectMapper with JavaTimeModule for quality module");
        return mapper;
    }
}
