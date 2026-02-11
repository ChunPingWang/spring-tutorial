package com.mes.boot.workorder;

import com.mes.boot.workorder.application.dto.CreateWorkOrderRequest;
import com.mes.boot.workorder.application.dto.WorkOrderResponse;
import com.mes.boot.workorder.application.service.WorkOrderApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

/**
 * [Spring Boot: @SpringBootApplication - 自動組態入口點]
 * [SOLID: SRP - 只負責啟動應用程式與初始化範例資料]
 *
 * MES 工單管理模組的啟動類別。
 * Module 1 展示 Spring Boot 2 基礎功能與 DDD 戰術設計模式。
 *
 * 啟動時透過 {@link ApplicationRunner} 自動初始化範例工單資料，
 * 方便學習者觀察系統行為與領域事件。
 */
@SpringBootApplication
public class MesBootBasicsApplication {

    private static final Logger log = LoggerFactory.getLogger(MesBootBasicsApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MesBootBasicsApplication.class, args);
    }

    /**
     * ApplicationRunner：在 Spring Boot 啟動完成後執行。
     * 建立範例工單資料，展示完整的工單生命週期。
     */
    @Bean
    public ApplicationRunner seedSampleData(WorkOrderApplicationService workOrderService) {
        return args -> {
            log.info("=== Seeding sample work order data ===");

            // 建立第一張工單 — 晶圓切割
            CreateWorkOrderRequest request1 = new CreateWorkOrderRequest(
                    "WAFER-001", "8吋晶圓", "8吋 P型 <100>",
                    1000, "HIGH",
                    LocalDate.now(), LocalDate.now().plusDays(7));
            WorkOrderResponse wo1 = workOrderService.createWorkOrder(request1);
            log.info("Sample WO1 created: {}", wo1.getId());

            // 建立第二張工單 — PCB 組裝
            CreateWorkOrderRequest request2 = new CreateWorkOrderRequest(
                    "PCB-002", "主機板 PCB", "ATX 標準尺寸",
                    500, "MEDIUM",
                    LocalDate.now().plusDays(1), LocalDate.now().plusDays(14));
            WorkOrderResponse wo2 = workOrderService.createWorkOrder(request2);
            log.info("Sample WO2 created: {}", wo2.getId());

            // 建立第三張工單 — 緊急訂單
            CreateWorkOrderRequest request3 = new CreateWorkOrderRequest(
                    "IC-003", "控制晶片", "ARM Cortex-M4",
                    200, "URGENT",
                    LocalDate.now(), LocalDate.now().plusDays(3));
            WorkOrderResponse wo3 = workOrderService.createWorkOrder(request3);
            log.info("Sample WO3 created: {}", wo3.getId());

            // 開始第一張工單
            workOrderService.startWorkOrder(wo1.getId());
            log.info("Sample WO1 started");

            // 完成第一張工單
            workOrderService.completeWorkOrder(wo1.getId(), 980, 15);
            log.info("Sample WO1 completed");

            log.info("=== Sample data seeding completed ===");
            log.info("Total work orders: {}", workOrderService.listWorkOrders().size());
        };
    }
}
