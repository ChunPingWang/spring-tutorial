package com.mes.mybatis.equipment.infrastructure.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * [Infrastructure Layer: Configuration - MyBatis 設定]
 *
 * 設定 MyBatis Mapper 介面的掃描路徑。
 * Spring Boot 的 mybatis-spring-boot-starter 會自動設定 SqlSessionFactory，
 * 我們只需指定 Mapper 介面所在的套件。
 */
@Configuration
@MapperScan("com.mes.mybatis.equipment.infrastructure.persistence.mybatis.mapper")
public class MyBatisConfig {
    // MyBatis 的其他設定（如 TypeHandler, Interceptor）可在此擴充
}
