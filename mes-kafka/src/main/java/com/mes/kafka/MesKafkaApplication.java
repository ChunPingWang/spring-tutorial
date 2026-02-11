package com.mes.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * [Spring Boot Application: MES Kafka 品質檢驗模組]
 *
 * Module 4 - 展示事件驅動架構與 Domain Events：
 * <ul>
 *   <li>Spring Cloud Stream Kafka Binder - 訊息發送與接收</li>
 *   <li>DDD Domain Events - 領域事件的產生與發佈</li>
 *   <li>Cross-Bounded Context Integration - 跨限界上下文整合</li>
 *   <li>CQRS - 命令查詢職責分離</li>
 *   <li>Hexagonal Architecture - 六角形架構</li>
 * </ul>
 */
@SpringBootApplication
public class MesKafkaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MesKafkaApplication.class, args);
    }
}
