package com.mes.boot.workorder.application.service;

import com.mes.boot.workorder.application.dto.CreateWorkOrderRequest;
import com.mes.boot.workorder.application.dto.WorkOrderResponse;
import com.mes.common.exception.DomainException;
import com.mes.common.exception.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * [DDD Pattern: Application Service 整合測試]
 * [SOLID: SRP - 測試應用服務的使用案例流程]
 *
 * 使用 @SpringBootTest 進行完整的 Spring Context 整合測試。
 * 驗證應用服務正確協調領域物件、Repository 與事件發佈。
 *
 * 注意：由於 ApplicationRunner 會在啟動時 seed 範例資料，
 * 測試中建立的工單會與範例資料共存。
 */
@SpringBootTest
@DisplayName("工單應用服務整合測試 (WorkOrderApplicationService)")
class WorkOrderApplicationServiceTest {

    @Autowired
    private WorkOrderApplicationService workOrderService;

    private CreateWorkOrderRequest createValidRequest() {
        return new CreateWorkOrderRequest(
                "TEST-001", "測試產品", "測試規格",
                500, "MEDIUM",
                LocalDate.now(), LocalDate.now().plusDays(7));
    }

    @Nested
    @DisplayName("建立工單")
    class CreateWorkOrder {

        @Test
        @DisplayName("使用有效請求應成功建立工單")
        void shouldCreateWorkOrder() {
            CreateWorkOrderRequest request = createValidRequest();

            WorkOrderResponse response = workOrderService.createWorkOrder(request);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isNotEmpty();
            assertThat(response.getStatus()).isEqualTo("CREATED");
            assertThat(response.getProductCode()).isEqualTo("TEST-001");
            assertThat(response.getProductName()).isEqualTo("測試產品");
            assertThat(response.getPlanned()).isEqualTo(500);
            assertThat(response.getCompleted()).isEqualTo(0);
            assertThat(response.getDefective()).isEqualTo(0);
            assertThat(response.getPriority()).isEqualTo("MEDIUM");
            assertThat(response.getCreatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("開始工單")
    class StartWorkOrder {

        @Test
        @DisplayName("應成功將工單從 CREATED 轉為 IN_PROGRESS")
        void shouldStartWorkOrder() {
            WorkOrderResponse created = workOrderService.createWorkOrder(createValidRequest());

            workOrderService.startWorkOrder(created.getId());

            WorkOrderResponse updated = workOrderService.getWorkOrder(created.getId());
            assertThat(updated.getStatus()).isEqualTo("IN_PROGRESS");
        }
    }

    @Nested
    @DisplayName("完成工單")
    class CompleteWorkOrder {

        @Test
        @DisplayName("應成功完成工單並更新數量")
        void shouldCompleteWorkOrder() {
            WorkOrderResponse created = workOrderService.createWorkOrder(createValidRequest());
            workOrderService.startWorkOrder(created.getId());

            workOrderService.completeWorkOrder(created.getId(), 480, 10);

            WorkOrderResponse updated = workOrderService.getWorkOrder(created.getId());
            assertThat(updated.getStatus()).isEqualTo("COMPLETED");
            assertThat(updated.getCompleted()).isEqualTo(480);
            assertThat(updated.getDefective()).isEqualTo(10);
        }
    }

    @Nested
    @DisplayName("取消工單")
    class CancelWorkOrder {

        @Test
        @DisplayName("應成功取消 CREATED 狀態的工單")
        void shouldCancelCreatedWorkOrder() {
            WorkOrderResponse created = workOrderService.createWorkOrder(createValidRequest());

            workOrderService.cancelWorkOrder(created.getId(), "客戶取消");

            WorkOrderResponse updated = workOrderService.getWorkOrder(created.getId());
            assertThat(updated.getStatus()).isEqualTo("CANCELLED");
        }

        @Test
        @DisplayName("取消已完成的工單應拋出 DomainException")
        void shouldRejectCancellingCompletedWorkOrder() {
            WorkOrderResponse created = workOrderService.createWorkOrder(createValidRequest());
            workOrderService.startWorkOrder(created.getId());
            workOrderService.completeWorkOrder(created.getId(), 480, 10);

            assertThatThrownBy(() -> workOrderService.cancelWorkOrder(created.getId(), "Test"))
                    .isInstanceOf(DomainException.class);
        }
    }

    @Nested
    @DisplayName("查詢工單")
    class QueryWorkOrders {

        @Test
        @DisplayName("根據 ID 查詢應回傳正確的工單")
        void shouldGetWorkOrderById() {
            WorkOrderResponse created = workOrderService.createWorkOrder(createValidRequest());

            WorkOrderResponse found = workOrderService.getWorkOrder(created.getId());

            assertThat(found.getId()).isEqualTo(created.getId());
            assertThat(found.getProductCode()).isEqualTo(created.getProductCode());
        }

        @Test
        @DisplayName("查詢不存在的工單應拋出 EntityNotFoundException")
        void shouldThrowWhenWorkOrderNotFound() {
            assertThatThrownBy(() -> workOrderService.getWorkOrder("non-existent-id"))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("WorkOrder");
        }

        @Test
        @DisplayName("列出所有工單應回傳非空列表")
        void shouldListAllWorkOrders() {
            workOrderService.createWorkOrder(createValidRequest());

            List<WorkOrderResponse> workOrders = workOrderService.listWorkOrders();

            assertThat(workOrders).isNotEmpty();
        }

        @Test
        @DisplayName("根據狀態列出工單應回傳正確結果")
        void shouldListByStatus() {
            workOrderService.createWorkOrder(createValidRequest());

            List<WorkOrderResponse> createdOrders = workOrderService.listByStatus("CREATED");

            assertThat(createdOrders).isNotEmpty();
            assertThat(createdOrders).allSatisfy(wo ->
                    assertThat(wo.getStatus()).isEqualTo("CREATED"));
        }
    }
}
