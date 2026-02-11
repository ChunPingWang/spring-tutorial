package com.mes.boot.workorder.domain.model;

import com.mes.boot.workorder.domain.event.WorkOrderCancelledEvent;
import com.mes.boot.workorder.domain.event.WorkOrderCompletedEvent;
import com.mes.boot.workorder.domain.event.WorkOrderCreatedEvent;
import com.mes.boot.workorder.domain.event.WorkOrderStartedEvent;
import com.mes.common.ddd.event.DomainEvent;
import com.mes.common.exception.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * [DDD Pattern: Aggregate Root 測試]
 * [SOLID: SRP - 測試工單聚合根的狀態轉換與領域事件]
 *
 * 測試 WorkOrder Aggregate Root 的核心行為：
 * - 狀態轉換規則
 * - 領域事件的註冊
 * - 非法狀態轉換的拒絕
 */
@DisplayName("工單聚合根 (WorkOrder Aggregate Root)")
class WorkOrderTest {

    private WorkOrder workOrder;

    @BeforeEach
    void setUp() {
        ProductInfo productInfo = new ProductInfo("WAFER-001", "8吋晶圓", "P型");
        Quantity quantity = Quantity.ofPlanned(1000);
        DateRange dateRange = new DateRange(LocalDate.now(), LocalDate.now().plusDays(7));

        workOrder = new WorkOrder(
                WorkOrderId.generate(), productInfo, quantity, Priority.HIGH, dateRange);
        // 清除建構時自動註冊的 WorkOrderCreatedEvent，方便後續測試驗證特定事件
        workOrder.clearEvents();
    }

    @Nested
    @DisplayName("建立工單")
    class Creation {

        @Test
        @DisplayName("新建的工單狀態應為 CREATED")
        void shouldHaveCreatedStatus() {
            assertThat(workOrder.getStatus()).isEqualTo(WorkOrderStatus.CREATED);
        }

        @Test
        @DisplayName("新建的工單應有有效的 ID")
        void shouldHaveValidId() {
            assertThat(workOrder.getId()).isNotNull();
            assertThat(workOrder.getId().getValue()).isNotEmpty();
        }

        @Test
        @DisplayName("新建的工單應記錄建立時間")
        void shouldHaveCreatedTimestamp() {
            assertThat(workOrder.getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("新建的工單應自動註冊 WorkOrderCreatedEvent")
        void shouldRaiseCreatedEventOnConstruction() {
            // 建立新的工單（不清除事件）
            WorkOrder newOrder = new WorkOrder(
                    WorkOrderId.generate(),
                    new ProductInfo("P001", "Product", "Spec"),
                    Quantity.ofPlanned(100),
                    Priority.MEDIUM,
                    new DateRange(LocalDate.now(), LocalDate.now().plusDays(5)));

            assertThat(newOrder.getDomainEvents()).hasSize(1);
            DomainEvent event = newOrder.getDomainEvents().get(0);
            assertThat(event).isInstanceOf(WorkOrderCreatedEvent.class);

            WorkOrderCreatedEvent createdEvent = (WorkOrderCreatedEvent) event;
            assertThat(createdEvent.getWorkOrderId()).isEqualTo(newOrder.getId().getValue());
            assertThat(createdEvent.getProductCode()).isEqualTo("P001");
        }
    }

    @Nested
    @DisplayName("開始生產 (start)")
    class Start {

        @Test
        @DisplayName("從 CREATED 狀態可以成功開始生產")
        void shouldTransitionToInProgress() {
            workOrder.start();

            assertThat(workOrder.getStatus()).isEqualTo(WorkOrderStatus.IN_PROGRESS);
        }

        @Test
        @DisplayName("開始生產後應註冊 WorkOrderStartedEvent")
        void shouldRaiseStartedEvent() {
            workOrder.start();

            assertThat(workOrder.getDomainEvents()).hasSize(1);
            DomainEvent event = workOrder.getDomainEvents().get(0);
            assertThat(event).isInstanceOf(WorkOrderStartedEvent.class);
            assertThat(event.getAggregateId()).isEqualTo(workOrder.getId().getValue());
        }

        @Test
        @DisplayName("已開始的工單不能再次開始")
        void shouldNotStartFromInProgress() {
            workOrder.start();

            assertThatThrownBy(() -> workOrder.start())
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("Cannot start work order");
        }

        @Test
        @DisplayName("已完成的工單不能開始")
        void shouldNotStartFromCompleted() {
            workOrder.start();
            workOrder.complete(new Quantity(1000, 900, 10));

            assertThatThrownBy(() -> workOrder.start())
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("Cannot start work order");
        }

        @Test
        @DisplayName("已取消的工單不能開始")
        void shouldNotStartFromCancelled() {
            workOrder.cancel("Test cancellation");

            assertThatThrownBy(() -> workOrder.start())
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("Cannot start work order");
        }
    }

    @Nested
    @DisplayName("完成生產 (complete)")
    class Complete {

        @BeforeEach
        void startWorkOrder() {
            workOrder.start();
            workOrder.clearEvents(); // 清除 start 事件，方便驗證 complete 事件
        }

        @Test
        @DisplayName("從 IN_PROGRESS 狀態可以成功完成生產")
        void shouldTransitionToCompleted() {
            Quantity actualQuantity = new Quantity(1000, 950, 20);
            workOrder.complete(actualQuantity);

            assertThat(workOrder.getStatus()).isEqualTo(WorkOrderStatus.COMPLETED);
            assertThat(workOrder.getQuantity().getCompleted()).isEqualTo(950);
            assertThat(workOrder.getQuantity().getDefective()).isEqualTo(20);
        }

        @Test
        @DisplayName("完成生產後應註冊 WorkOrderCompletedEvent")
        void shouldRaiseCompletedEvent() {
            Quantity actualQuantity = new Quantity(1000, 950, 20);
            workOrder.complete(actualQuantity);

            assertThat(workOrder.getDomainEvents()).hasSize(1);
            DomainEvent event = workOrder.getDomainEvents().get(0);
            assertThat(event).isInstanceOf(WorkOrderCompletedEvent.class);

            WorkOrderCompletedEvent completedEvent = (WorkOrderCompletedEvent) event;
            assertThat(completedEvent.getCompleted()).isEqualTo(950);
            assertThat(completedEvent.getDefective()).isEqualTo(20);
        }

        @Test
        @DisplayName("完成量不能超過計畫量")
        void shouldRejectCompletedExceedingPlanned() {
            Quantity actualQuantity = new Quantity(1000, 1100, 0);

            assertThatThrownBy(() -> workOrder.complete(actualQuantity))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("cannot exceed planned quantity");
        }

        @Test
        @DisplayName("從 CREATED 狀態不能直接完成")
        void shouldNotCompleteFromCreated() {
            WorkOrder newOrder = new WorkOrder(
                    WorkOrderId.generate(),
                    new ProductInfo("P001", "Product", "Spec"),
                    Quantity.ofPlanned(100),
                    Priority.MEDIUM,
                    new DateRange(LocalDate.now(), LocalDate.now().plusDays(5)));

            assertThatThrownBy(() -> newOrder.complete(new Quantity(100, 80, 5)))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("Cannot complete work order");
        }
    }

    @Nested
    @DisplayName("取消工單 (cancel)")
    class Cancel {

        @Test
        @DisplayName("從 CREATED 狀態可以取消工單")
        void shouldCancelFromCreated() {
            workOrder.cancel("客戶取消訂單");

            assertThat(workOrder.getStatus()).isEqualTo(WorkOrderStatus.CANCELLED);
        }

        @Test
        @DisplayName("從 IN_PROGRESS 狀態可以取消工單")
        void shouldCancelFromInProgress() {
            workOrder.start();
            workOrder.clearEvents();

            workOrder.cancel("物料不足");

            assertThat(workOrder.getStatus()).isEqualTo(WorkOrderStatus.CANCELLED);
        }

        @Test
        @DisplayName("取消工單後應註冊 WorkOrderCancelledEvent 且包含原因")
        void shouldRaiseCancelledEventWithReason() {
            String reason = "設備故障";
            workOrder.cancel(reason);

            assertThat(workOrder.getDomainEvents()).hasSize(1);
            DomainEvent event = workOrder.getDomainEvents().get(0);
            assertThat(event).isInstanceOf(WorkOrderCancelledEvent.class);

            WorkOrderCancelledEvent cancelledEvent = (WorkOrderCancelledEvent) event;
            assertThat(cancelledEvent.getReason()).isEqualTo(reason);
        }

        @Test
        @DisplayName("已完成的工單不能取消")
        void shouldNotCancelCompleted() {
            workOrder.start();
            workOrder.complete(new Quantity(1000, 900, 10));

            assertThatThrownBy(() -> workOrder.cancel("Test"))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("Cannot cancel a completed work order");
        }

        @Test
        @DisplayName("已取消的工單不能再次取消")
        void shouldNotCancelAlreadyCancelled() {
            workOrder.cancel("First cancel");

            assertThatThrownBy(() -> workOrder.cancel("Second cancel"))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("already cancelled");
        }
    }

    @Nested
    @DisplayName("領域事件管理")
    class DomainEvents {

        @Test
        @DisplayName("clearEvents 應清除所有已註冊的事件")
        void shouldClearEvents() {
            workOrder.start();
            assertThat(workOrder.getDomainEvents()).isNotEmpty();

            workOrder.clearEvents();
            assertThat(workOrder.getDomainEvents()).isEmpty();
        }

        @Test
        @DisplayName("getDomainEvents 回傳的列表應為唯讀")
        void shouldReturnUnmodifiableEventList() {
            workOrder.start();

            assertThatThrownBy(() -> workOrder.getDomainEvents().clear())
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
