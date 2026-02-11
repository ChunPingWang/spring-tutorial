package com.mes.mybatis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * [Module 3: MyBatis 持久層 - 設備管理]
 *
 * 本模組展示：
 * 1. MyBatis 作為六角形架構的基礎設施適配器（Output Adapter）
 * 2. DDD 戰術模式：Aggregate Root, Entity, Value Object, Domain Event, Repository, Factory
 * 3. CQRS 讀寫分離：Command 走 Domain Model，Query 可直接走 Mapper
 * 4. 資料物件（DO）與領域物件（Domain Object）的轉換
 *
 * 架構分層：
 * - domain: 領域模型（不依賴任何框架）
 * - application: 應用層（Command/Query Handler, Assembler）
 * - infrastructure: 基礎設施層（MyBatis Mapper, Converter, Repository 實作）
 * - adapter: 適配器層（REST Controller）
 */
@SpringBootApplication
public class MesMyBatisApplication {

    public static void main(String[] args) {
        SpringApplication.run(MesMyBatisApplication.class, args);
    }
}
