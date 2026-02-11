package com.mes.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * [Spring Boot: Application Entry Point]
 *
 * MES Web API 模組的啟動類別。
 * 展示 Spring Boot 2 Web API + 完整 CQRS 模式的生產追蹤系統。
 *
 * 架構特點：
 * - DDD 戰術設計模式（Aggregate Root, Value Object, Domain Event, Repository）
 * - 六角形架構（Hexagonal Architecture / Ports and Adapters）
 * - 完整 CQRS（Command/Query 分離，各有獨立的 Bus 和 Handler）
 */
@SpringBootApplication
public class MesWebApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MesWebApiApplication.class, args);
    }
}
