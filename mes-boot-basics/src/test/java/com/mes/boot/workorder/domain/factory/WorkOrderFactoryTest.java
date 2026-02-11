package com.mes.boot.workorder.domain.factory;

import com.mes.boot.workorder.domain.event.WorkOrderCreatedEvent;
import com.mes.boot.workorder.domain.model.DateRange;
import com.mes.boot.workorder.domain.model.Priority;
import com.mes.boot.workorder.domain.model.ProductInfo;
import com.mes.boot.workorder.domain.model.Quantity;
import com.mes.boot.workorder.domain.model.WorkOrder;
import com.mes.boot.workorder.domain.model.WorkOrderStatus;
import com.mes.common.ddd.event.DomainEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * [DDD Pattern: Factory 測試]
 * [SOLID: SRP - 測試工廠方法的建立邏輯與驗證規則]
 *
 * 測試 WorkOrderFactory 的核心行為：
 * - 成功建立工單並註冊 WorkOrderCreatedEvent
 * - 參數驗證（null 檢查、業務規則）
 */
@DisplayName("工單工廠 (WorkOrderFactory)")
class WorkOrderFactoryTest {

    private final ProductInfo validProductInfo = new ProductInfo("WAFER-001", "8吋晶圓", "P型");
    private final Quantity validQuantity = Quantity.ofPlanned(1000);
    private final Priority validPriority = Priority.HIGH;
    private final DateRange validDateRange = new DateRange(LocalDate.now(), LocalDate.now().plusDays(7));

    @Nested
    @DisplayName("成功建立工單")
    class SuccessfulCreation {

        @Test
        @DisplayName("使用有效參數應成功建立工單")
        void shouldCreateWorkOrderWithValidParams() {
            WorkOrder workOrder = WorkOrderFactory.create(
                    validProductInfo, validQuantity, validPriority, validDateRange);

            assertThat(workOrder).isNotNull();
            assertThat(workOrder.getId()).isNotNull();
            assertThat(workOrder.getStatus()).isEqualTo(WorkOrderStatus.CREATED);
            assertThat(workOrder.getProductInfo()).isEqualTo(validProductInfo);
            assertThat(workOrder.getQuantity()).isEqualTo(validQuantity);
            assertThat(workOrder.getPriority()).isEqualTo(validPriority);
            assertThat(workOrder.getDateRange()).isEqualTo(validDateRange);
        }

        @Test
        @DisplayName("建立工單後應自動註冊 WorkOrderCreatedEvent")
        void shouldRegisterCreatedEvent() {
            WorkOrder workOrder = WorkOrderFactory.create(
                    validProductInfo, validQuantity, validPriority, validDateRange);

            assertThat(workOrder.getDomainEvents()).hasSize(1);

            DomainEvent event = workOrder.getDomainEvents().get(0);
            assertThat(event).isInstanceOf(WorkOrderCreatedEvent.class);

            WorkOrderCreatedEvent createdEvent = (WorkOrderCreatedEvent) event;
            assertThat(createdEvent.getWorkOrderId()).isEqualTo(workOrder.getId().getValue());
            assertThat(createdEvent.getProductCode()).isEqualTo("WAFER-001");
        }

        @Test
        @DisplayName("每次建立的工單應有不同的 ID")
        void shouldGenerateUniqueIds() {
            WorkOrder wo1 = WorkOrderFactory.create(
                    validProductInfo, validQuantity, validPriority, validDateRange);
            WorkOrder wo2 = WorkOrderFactory.create(
                    validProductInfo, validQuantity, validPriority, validDateRange);

            assertThat(wo1.getId()).isNotEqualTo(wo2.getId());
        }
    }

    @Nested
    @DisplayName("參數驗證")
    class ParameterValidation {

        @Test
        @DisplayName("產品資訊為 null 時應拋出 NullPointerException")
        void shouldRejectNullProductInfo() {
            assertThatThrownBy(() -> WorkOrderFactory.create(
                    null, validQuantity, validPriority, validDateRange))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Product info must not be null");
        }

        @Test
        @DisplayName("數量為 null 時應拋出 NullPointerException")
        void shouldRejectNullQuantity() {
            assertThatThrownBy(() -> WorkOrderFactory.create(
                    validProductInfo, null, validPriority, validDateRange))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Quantity must not be null");
        }

        @Test
        @DisplayName("優先順序為 null 時應拋出 NullPointerException")
        void shouldRejectNullPriority() {
            assertThatThrownBy(() -> WorkOrderFactory.create(
                    validProductInfo, validQuantity, null, validDateRange))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Priority must not be null");
        }

        @Test
        @DisplayName("日期範圍為 null 時應拋出 NullPointerException")
        void shouldRejectNullDateRange() {
            assertThatThrownBy(() -> WorkOrderFactory.create(
                    validProductInfo, validQuantity, validPriority, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Date range must not be null");
        }

        @Test
        @DisplayName("計畫量為零時應拋出 IllegalArgumentException")
        void shouldRejectZeroPlannedQuantity() {
            Quantity zeroQuantity = Quantity.ofPlanned(0);

            assertThatThrownBy(() -> WorkOrderFactory.create(
                    validProductInfo, zeroQuantity, validPriority, validDateRange))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Planned quantity must be greater than 0");
        }
    }
}
