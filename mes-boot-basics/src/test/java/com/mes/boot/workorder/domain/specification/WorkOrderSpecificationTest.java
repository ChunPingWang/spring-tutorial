package com.mes.boot.workorder.domain.specification;

import com.mes.boot.workorder.domain.model.DateRange;
import com.mes.boot.workorder.domain.model.Priority;
import com.mes.boot.workorder.domain.model.ProductInfo;
import com.mes.boot.workorder.domain.model.Quantity;
import com.mes.boot.workorder.domain.model.WorkOrder;
import com.mes.boot.workorder.domain.model.WorkOrderId;
import com.mes.common.ddd.specification.Specification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * [DDD Pattern: Specification 測試]
 * [SOLID: OCP - 測試 Specification 的組合能力（and, or, not）]
 *
 * 測試工單 Specification 的核心行為：
 * - OverdueWorkOrderSpec：逾期工單判斷
 * - HighPriorityWorkOrderSpec：高優先工單判斷
 * - 規格組合（AND, OR, NOT）
 */
@DisplayName("工單規格模式 (Specification Pattern)")
class WorkOrderSpecificationTest {

    private WorkOrder createWorkOrder(Priority priority, LocalDate start, LocalDate end) {
        return new WorkOrder(
                WorkOrderId.generate(),
                new ProductInfo("P001", "Product", "Spec"),
                Quantity.ofPlanned(100),
                priority,
                new DateRange(start, end));
    }

    @Nested
    @DisplayName("逾期工單規格 (OverdueWorkOrderSpec)")
    class OverdueSpec {

        private final OverdueWorkOrderSpec spec = new OverdueWorkOrderSpec();

        @Test
        @DisplayName("計畫結束日期已過且未完成的工單應為逾期")
        void shouldDetectOverdueWorkOrder() {
            WorkOrder overdueOrder = createWorkOrder(
                    Priority.MEDIUM,
                    LocalDate.now().minusDays(10),
                    LocalDate.now().minusDays(1));

            assertThat(spec.isSatisfiedBy(overdueOrder)).isTrue();
        }

        @Test
        @DisplayName("計畫結束日期未到的工單不應為逾期")
        void shouldNotDetectFutureWorkOrder() {
            WorkOrder futureOrder = createWorkOrder(
                    Priority.MEDIUM,
                    LocalDate.now(),
                    LocalDate.now().plusDays(7));

            assertThat(spec.isSatisfiedBy(futureOrder)).isFalse();
        }

        @Test
        @DisplayName("已完成的工單即使日期已過也不應為逾期")
        void shouldNotDetectCompletedWorkOrder() {
            WorkOrder completedOrder = createWorkOrder(
                    Priority.MEDIUM,
                    LocalDate.now().minusDays(10),
                    LocalDate.now().minusDays(1));
            completedOrder.start();
            completedOrder.complete(new Quantity(100, 90, 5));

            assertThat(spec.isSatisfiedBy(completedOrder)).isFalse();
        }

        @Test
        @DisplayName("已取消的工單即使日期已過也不應為逾期")
        void shouldNotDetectCancelledWorkOrder() {
            WorkOrder cancelledOrder = createWorkOrder(
                    Priority.MEDIUM,
                    LocalDate.now().minusDays(10),
                    LocalDate.now().minusDays(1));
            cancelledOrder.cancel("Test");

            assertThat(spec.isSatisfiedBy(cancelledOrder)).isFalse();
        }
    }

    @Nested
    @DisplayName("高優先工單規格 (HighPriorityWorkOrderSpec)")
    class HighPrioritySpec {

        private final HighPriorityWorkOrderSpec spec = new HighPriorityWorkOrderSpec();

        @Test
        @DisplayName("HIGH 優先的工單應滿足條件")
        void shouldSatisfyForHighPriority() {
            WorkOrder highOrder = createWorkOrder(
                    Priority.HIGH, LocalDate.now(), LocalDate.now().plusDays(7));

            assertThat(spec.isSatisfiedBy(highOrder)).isTrue();
        }

        @Test
        @DisplayName("URGENT 優先的工單應滿足條件")
        void shouldSatisfyForUrgentPriority() {
            WorkOrder urgentOrder = createWorkOrder(
                    Priority.URGENT, LocalDate.now(), LocalDate.now().plusDays(7));

            assertThat(spec.isSatisfiedBy(urgentOrder)).isTrue();
        }

        @Test
        @DisplayName("MEDIUM 優先的工單不應滿足條件")
        void shouldNotSatisfyForMediumPriority() {
            WorkOrder mediumOrder = createWorkOrder(
                    Priority.MEDIUM, LocalDate.now(), LocalDate.now().plusDays(7));

            assertThat(spec.isSatisfiedBy(mediumOrder)).isFalse();
        }

        @Test
        @DisplayName("LOW 優先的工單不應滿足條件")
        void shouldNotSatisfyForLowPriority() {
            WorkOrder lowOrder = createWorkOrder(
                    Priority.LOW, LocalDate.now(), LocalDate.now().plusDays(7));

            assertThat(spec.isSatisfiedBy(lowOrder)).isFalse();
        }
    }

    @Nested
    @DisplayName("規格組合 (Specification Composition)")
    class Composition {

        @Test
        @DisplayName("AND 組合：逾期且高優先的工單")
        void shouldCombineWithAnd() {
            Specification<WorkOrder> criticalSpec =
                    new OverdueWorkOrderSpec().and(new HighPriorityWorkOrderSpec());

            // 逾期且高優先 → 滿足
            WorkOrder overdueHighPriority = createWorkOrder(
                    Priority.HIGH,
                    LocalDate.now().minusDays(10),
                    LocalDate.now().minusDays(1));
            assertThat(criticalSpec.isSatisfiedBy(overdueHighPriority)).isTrue();

            // 逾期但低優先 → 不滿足
            WorkOrder overdueLowPriority = createWorkOrder(
                    Priority.LOW,
                    LocalDate.now().minusDays(10),
                    LocalDate.now().minusDays(1));
            assertThat(criticalSpec.isSatisfiedBy(overdueLowPriority)).isFalse();

            // 高優先但未逾期 → 不滿足
            WorkOrder futureHighPriority = createWorkOrder(
                    Priority.HIGH,
                    LocalDate.now(),
                    LocalDate.now().plusDays(7));
            assertThat(criticalSpec.isSatisfiedBy(futureHighPriority)).isFalse();
        }

        @Test
        @DisplayName("OR 組合：逾期或高優先的工單")
        void shouldCombineWithOr() {
            Specification<WorkOrder> importantSpec =
                    new OverdueWorkOrderSpec().or(new HighPriorityWorkOrderSpec());

            // 逾期且高優先 → 滿足
            WorkOrder overdueHighPriority = createWorkOrder(
                    Priority.HIGH,
                    LocalDate.now().minusDays(10),
                    LocalDate.now().minusDays(1));
            assertThat(importantSpec.isSatisfiedBy(overdueHighPriority)).isTrue();

            // 逾期但低優先 → 滿足（因為逾期）
            WorkOrder overdueLowPriority = createWorkOrder(
                    Priority.LOW,
                    LocalDate.now().minusDays(10),
                    LocalDate.now().minusDays(1));
            assertThat(importantSpec.isSatisfiedBy(overdueLowPriority)).isTrue();

            // 高優先但未逾期 → 滿足（因為高優先）
            WorkOrder futureHighPriority = createWorkOrder(
                    Priority.HIGH,
                    LocalDate.now(),
                    LocalDate.now().plusDays(7));
            assertThat(importantSpec.isSatisfiedBy(futureHighPriority)).isTrue();

            // 未逾期且低優先 → 不滿足
            WorkOrder futureLowPriority = createWorkOrder(
                    Priority.LOW,
                    LocalDate.now(),
                    LocalDate.now().plusDays(7));
            assertThat(importantSpec.isSatisfiedBy(futureLowPriority)).isFalse();
        }

        @Test
        @DisplayName("NOT 組合：非高優先的工單")
        void shouldNegateWithNot() {
            Specification<WorkOrder> notHighPrioritySpec =
                    new HighPriorityWorkOrderSpec().not();

            WorkOrder lowOrder = createWorkOrder(
                    Priority.LOW, LocalDate.now(), LocalDate.now().plusDays(7));
            assertThat(notHighPrioritySpec.isSatisfiedBy(lowOrder)).isTrue();

            WorkOrder highOrder = createWorkOrder(
                    Priority.HIGH, LocalDate.now(), LocalDate.now().plusDays(7));
            assertThat(notHighPrioritySpec.isSatisfiedBy(highOrder)).isFalse();
        }
    }
}
