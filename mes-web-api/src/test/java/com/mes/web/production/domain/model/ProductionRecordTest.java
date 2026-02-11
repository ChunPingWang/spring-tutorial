package com.mes.web.production.domain.model;

import com.mes.common.ddd.event.DomainEvent;
import com.mes.common.exception.BusinessRuleViolationException;
import com.mes.common.exception.DomainException;
import com.mes.web.production.domain.event.DefectRecordedEvent;
import com.mes.web.production.domain.event.ProductionCompletedEvent;
import com.mes.web.production.domain.event.ProductionStartedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * [DDD Pattern: Aggregate Root 單元測試]
 *
 * 測試 ProductionRecord 聚合根的：
 * 1. 建構與初始狀態
 * 2. 狀態轉換規則
 * 3. 領域事件的註冊
 * 4. 業務規則的驗證
 */
@DisplayName("ProductionRecord 聚合根測試")
class ProductionRecordTest {

    private ProductionRecord record;

    @BeforeEach
    void setUp() {
        ProductionRecordId id = ProductionRecordId.of("PR-001");
        ProductionLineId lineId = ProductionLineId.of("LINE-A");
        ProductionLine line = new ProductionLine(lineId, "A 產線");
        OperatorInfo operator = new OperatorInfo("OP-001", "王小明", "DAY");

        record = new ProductionRecord(id, line, "WO-001", "PROD-A", operator);
    }

    @Nested
    @DisplayName("建構與初始狀態測試")
    class ConstructionTests {

        @Test
        @DisplayName("新建的生產紀錄應為 PENDING 狀態")
        void shouldBeInPendingStatus() {
            assertThat(record.getStatus()).isEqualTo(ProductionStatus.PENDING);
        }

        @Test
        @DisplayName("新建的生產紀錄產出應為零")
        void shouldHaveZeroOutput() {
            assertThat(record.getOutput().getTotal()).isEqualTo(0);
        }

        @Test
        @DisplayName("新建的生產紀錄不應有領域事件")
        void shouldHaveNoDomainEvents() {
            assertThat(record.getDomainEvents()).isEmpty();
        }

        @Test
        @DisplayName("產線不可為空")
        void shouldRejectNullProductionLine() {
            ProductionRecordId id = ProductionRecordId.of("PR-002");
            OperatorInfo operator = new OperatorInfo("OP-001", "王小明", "DAY");

            assertThatThrownBy(() -> new ProductionRecord(id, null, "WO-001", "PROD-A", operator))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("產線不可為空");
        }

        @Test
        @DisplayName("工單 ID 不可為空")
        void shouldRejectNullWorkOrderId() {
            ProductionRecordId id = ProductionRecordId.of("PR-002");
            ProductionLine line = new ProductionLine(ProductionLineId.of("L-1"), "Line 1");
            OperatorInfo operator = new OperatorInfo("OP-001", "王小明", "DAY");

            assertThatThrownBy(() -> new ProductionRecord(id, line, null, "PROD-A", operator))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("工單 ID 不可為空");
        }
    }

    @Nested
    @DisplayName("啟動生產 (start) 測試")
    class StartTests {

        @Test
        @DisplayName("啟動後狀態應變為 RUNNING")
        void shouldTransitionToRunning() {
            record.start();
            assertThat(record.getStatus()).isEqualTo(ProductionStatus.RUNNING);
        }

        @Test
        @DisplayName("啟動後應註冊 ProductionStartedEvent")
        void shouldRegisterStartedEvent() {
            record.start();
            List<DomainEvent> events = record.getDomainEvents();

            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(ProductionStartedEvent.class);

            ProductionStartedEvent event = (ProductionStartedEvent) events.get(0);
            assertThat(event.getAggregateId()).isEqualTo("PR-001");
            assertThat(event.getWorkOrderId()).isEqualTo("WO-001");
        }

        @Test
        @DisplayName("非 PENDING 狀態不可啟動")
        void shouldRejectStartFromNonPendingStatus() {
            record.start(); // PENDING -> RUNNING

            assertThatThrownBy(() -> record.start())
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("待開始");
        }
    }

    @Nested
    @DisplayName("暫停生產 (pause) 測試")
    class PauseTests {

        @Test
        @DisplayName("RUNNING 狀態可以暫停")
        void shouldTransitionToPaused() {
            record.start();
            record.pause();
            assertThat(record.getStatus()).isEqualTo(ProductionStatus.PAUSED);
        }

        @Test
        @DisplayName("非 RUNNING 狀態不可暫停")
        void shouldRejectPauseFromNonRunningStatus() {
            assertThatThrownBy(() -> record.pause())
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("進行中");
        }
    }

    @Nested
    @DisplayName("恢復生產 (resume) 測試")
    class ResumeTests {

        @Test
        @DisplayName("PAUSED 狀態可以恢復")
        void shouldTransitionToRunning() {
            record.start();
            record.pause();
            record.resume();
            assertThat(record.getStatus()).isEqualTo(ProductionStatus.RUNNING);
        }

        @Test
        @DisplayName("非 PAUSED 狀態不可恢復")
        void shouldRejectResumeFromNonPausedStatus() {
            record.start();

            assertThatThrownBy(() -> record.resume())
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("已暫停");
        }
    }

    @Nested
    @DisplayName("記錄產出 (recordOutput) 測試")
    class RecordOutputTests {

        @Test
        @DisplayName("記錄產出應更新輸出數據")
        void shouldUpdateOutput() {
            OutputQuantity output = new OutputQuantity(100, 5, 3);
            record.recordOutput(output);

            assertThat(record.getOutput().getGood()).isEqualTo(100);
            assertThat(record.getOutput().getDefective()).isEqualTo(5);
            assertThat(record.getOutput().getRework()).isEqualTo(3);
        }

        @Test
        @DisplayName("有不良品時應註冊 DefectRecordedEvent")
        void shouldRegisterDefectEventWhenDefectiveGreaterThanZero() {
            OutputQuantity output = new OutputQuantity(90, 10, 0);
            record.recordOutput(output);

            List<DomainEvent> events = record.getDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(DefectRecordedEvent.class);

            DefectRecordedEvent event = (DefectRecordedEvent) events.get(0);
            assertThat(event.getDefectCount()).isEqualTo(10);
        }

        @Test
        @DisplayName("無不良品時不應註冊 DefectRecordedEvent")
        void shouldNotRegisterDefectEventWhenNoDefective() {
            OutputQuantity output = new OutputQuantity(100, 0, 0);
            record.recordOutput(output);

            assertThat(record.getDomainEvents()).isEmpty();
        }
    }

    @Nested
    @DisplayName("完成生產 (finish) 測試")
    class FinishTests {

        @Test
        @DisplayName("RUNNING 狀態可以完成")
        void shouldTransitionToFinished() {
            record.start();
            record.finish();
            assertThat(record.getStatus()).isEqualTo(ProductionStatus.FINISHED);
        }

        @Test
        @DisplayName("完成後應註冊 ProductionCompletedEvent")
        void shouldRegisterCompletedEvent() {
            record.start();
            record.clearEvents(); // 清除 start 事件
            record.finish();

            List<DomainEvent> events = record.getDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(ProductionCompletedEvent.class);

            ProductionCompletedEvent event = (ProductionCompletedEvent) events.get(0);
            assertThat(event.getWorkOrderId()).isEqualTo("WO-001");
            assertThat(event.getProductCode()).isEqualTo("PROD-A");
        }

        @Test
        @DisplayName("非 RUNNING 狀態不可完成")
        void shouldRejectFinishFromNonRunningStatus() {
            assertThatThrownBy(() -> record.finish())
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("進行中");
        }
    }

    @Nested
    @DisplayName("製程步驟 (addStep) 測試")
    class StepTests {

        @Test
        @DisplayName("可新增製程步驟")
        void shouldAddProcessStep() {
            ProcessStep step = new ProcessStep(1, "上料", 10);
            record.addStep(step);

            assertThat(record.getSteps()).hasSize(1);
            assertThat(record.getSteps().get(0).getStepName()).isEqualTo("上料");
        }

        @Test
        @DisplayName("步驟列表應為不可變")
        void shouldReturnUnmodifiableList() {
            ProcessStep step = new ProcessStep(1, "上料", 10);
            record.addStep(step);

            assertThatThrownBy(() -> record.getSteps().add(new ProcessStep(2, "加工", 20)))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
