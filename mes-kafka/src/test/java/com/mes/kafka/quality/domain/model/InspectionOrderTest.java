package com.mes.kafka.quality.domain.model;

import com.mes.common.ddd.event.DomainEvent;
import com.mes.common.exception.BusinessRuleViolationException;
import com.mes.common.exception.DomainException;
import com.mes.kafka.quality.domain.event.DefectDetectedEvent;
import com.mes.kafka.quality.domain.event.InspectionCompletedEvent;
import com.mes.kafka.quality.domain.event.InspectionOrderCreatedEvent;
import com.mes.kafka.quality.domain.event.QualityAlertEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * InspectionOrder 聚合根單元測試。
 * 驗證狀態轉換、業務規則、領域事件等核心邏輯。
 */
@DisplayName("InspectionOrder 聚合根測試")
class InspectionOrderTest {

    private InspectionOrder order;
    private QualityStandard standard;
    private MeasuredValue passingValue;
    private MeasuredValue failingValue;

    @BeforeEach
    void setUp() {
        order = new InspectionOrder(
                new InspectionOrderId("INS-001"),
                "WO-001",
                "PRODUCT-A",
                InspectionType.FINAL);

        standard = new QualityStandard("DIM-001", 9.95, 10.05, "mm");
        passingValue = new MeasuredValue(10.00, "mm", LocalDateTime.now(), "OP-001");
        failingValue = new MeasuredValue(10.10, "mm", LocalDateTime.now(), "OP-001");
    }

    @Nested
    @DisplayName("狀態轉換測試")
    class StateTransitionTest {

        @Test
        @DisplayName("新建工單初始狀態應為 PENDING")
        void shouldInitializeWithPendingStatus() {
            assertThat(order.getStatus()).isEqualTo(InspectionStatus.PENDING);
        }

        @Test
        @DisplayName("開始檢驗應將狀態從 PENDING 轉為 IN_PROGRESS")
        void shouldTransitionFromPendingToInProgress() {
            order.startInspection();
            assertThat(order.getStatus()).isEqualTo(InspectionStatus.IN_PROGRESS);
        }

        @Test
        @DisplayName("已開始的檢驗不能再次開始")
        void shouldNotStartInspectionTwice() {
            order.startInspection();
            assertThatThrownBy(() -> order.startInspection())
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("Cannot start inspection");
        }

        @Test
        @DisplayName("暫停檢驗應將狀態從 IN_PROGRESS 轉為 ON_HOLD")
        void shouldTransitionToOnHold() {
            order.startInspection();
            order.putOnHold("設備故障");
            assertThat(order.getStatus()).isEqualTo(InspectionStatus.ON_HOLD);
        }

        @Test
        @DisplayName("非 IN_PROGRESS 狀態不能暫停")
        void shouldNotPutOnHoldWhenNotInProgress() {
            assertThatThrownBy(() -> order.putOnHold("test"))
                    .isInstanceOf(DomainException.class);
        }
    }

    @Nested
    @DisplayName("記錄檢驗結果測試")
    class RecordResultTest {

        @BeforeEach
        void startOrder() {
            order.startInspection();
            order.clearEvents(); // 清除 startInspection 產生的事件
        }

        @Test
        @DisplayName("記錄合格的檢驗結果")
        void shouldRecordPassingResult() {
            order.recordResult(standard, passingValue);

            assertThat(order.getResults()).hasSize(1);
            assertThat(order.getResults().get(0).isPassed()).isTrue();
            assertThat(order.getDomainEvents()).isEmpty();
        }

        @Test
        @DisplayName("記錄不合格的檢驗結果應觸發 DefectDetectedEvent")
        void shouldRaiseDefectDetectedEventForFailingResult() {
            order.recordResult(standard, failingValue);

            assertThat(order.getResults()).hasSize(1);
            assertThat(order.getResults().get(0).isPassed()).isFalse();
            assertThat(order.getDomainEvents()).hasSize(1);
            assertThat(order.getDomainEvents().get(0)).isInstanceOf(DefectDetectedEvent.class);
        }

        @Test
        @DisplayName("記錄帶缺陷詳情的檢驗結果")
        void shouldRecordResultWithDefectDetail() {
            DefectDetail defect = new DefectDetail("DEF-001", "尺寸偏差", "MAJOR", "超出上限");
            order.recordResultWithDefect(standard, failingValue, defect);

            assertThat(order.getResults()).hasSize(1);
            InspectionResult result = order.getResults().get(0);
            assertThat(result.isPassed()).isFalse();
            assertThat(result.getDefectDetail()).isNotNull();
            assertThat(result.getDefectDetail().getDefectCode()).isEqualTo("DEF-001");

            List<DomainEvent> events = order.getDomainEvents();
            assertThat(events).hasSize(1);
            DefectDetectedEvent event = (DefectDetectedEvent) events.get(0);
            assertThat(event.getDefectCode()).isEqualTo("DEF-001");
        }

        @Test
        @DisplayName("非 IN_PROGRESS 狀態不能記錄結果")
        void shouldNotRecordResultWhenNotInProgress() {
            order.recordResult(standard, passingValue);
            order.complete();
            assertThatThrownBy(() -> order.recordResult(standard, passingValue))
                    .isInstanceOf(DomainException.class);
        }
    }

    @Nested
    @DisplayName("完成檢驗測試")
    class CompleteTest {

        @BeforeEach
        void startOrder() {
            order.startInspection();
            order.clearEvents();
        }

        @Test
        @DisplayName("全部合格應判定為 PASSED")
        void shouldPassWhenAllResultsPass() {
            order.recordResult(standard, passingValue);
            order.recordResult(standard, new MeasuredValue(10.02, "mm", LocalDateTime.now(), "OP-001"));
            order.clearEvents();

            order.complete();

            assertThat(order.getStatus()).isEqualTo(InspectionStatus.PASSED);
            assertThat(order.getDefectRate()).isEqualTo(0.0);
        }

        @Test
        @DisplayName("有不合格結果應判定為 FAILED")
        void shouldFailWhenAnyResultFails() {
            order.recordResult(standard, passingValue);
            order.recordResult(standard, failingValue);
            order.clearEvents();

            order.complete();

            assertThat(order.getStatus()).isEqualTo(InspectionStatus.FAILED);
            assertThat(order.getDefectRate()).isEqualTo(0.5);
        }

        @Test
        @DisplayName("無檢驗結果時不能完成")
        void shouldNotCompleteWithoutResults() {
            assertThatThrownBy(() -> order.complete())
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("without any results");
        }

        @Test
        @DisplayName("完成時應觸發 InspectionCompletedEvent")
        void shouldRaiseInspectionCompletedEvent() {
            order.recordResult(standard, passingValue);
            order.clearEvents();

            order.complete();

            List<DomainEvent> events = order.getDomainEvents();
            assertThat(events).anyMatch(e -> e instanceof InspectionCompletedEvent);
        }
    }

    @Nested
    @DisplayName("品質警報測試")
    class QualityAlertTest {

        @Test
        @DisplayName("不良率超過閾值應觸發 QualityAlertEvent")
        void shouldRaiseQualityAlertWhenDefectRateExceedsThreshold() {
            // 使用低閾值的工單
            InspectionOrder alertOrder = new InspectionOrder(
                    new InspectionOrderId("INS-002"), "WO-002", "PRODUCT-B",
                    InspectionType.SAMPLING, 0.10);
            alertOrder.startInspection();
            alertOrder.clearEvents();

            // 記錄 10 筆結果，2 筆不合格 (20% > 10%)
            for (int i = 0; i < 8; i++) {
                alertOrder.recordResult(standard,
                        new MeasuredValue(10.00, "mm", LocalDateTime.now(), "OP-001"));
            }
            alertOrder.recordResult(standard, failingValue);
            alertOrder.recordResult(standard, failingValue);
            alertOrder.clearEvents();

            alertOrder.complete();

            List<DomainEvent> events = alertOrder.getDomainEvents();
            assertThat(events).anyMatch(e -> e instanceof QualityAlertEvent);

            QualityAlertEvent alertEvent = null;
            for (DomainEvent event : events) {
                if (event instanceof QualityAlertEvent) {
                    alertEvent = (QualityAlertEvent) event;
                    break;
                }
            }
            assertThat(alertEvent).isNotNull();
            assertThat(alertEvent.getDefectRate()).isEqualTo(0.2);
            assertThat(alertEvent.getProductCode()).isEqualTo("PRODUCT-B");
        }

        @Test
        @DisplayName("不良率未超過閾值不應觸發 QualityAlertEvent")
        void shouldNotRaiseQualityAlertWhenDefectRateBelowThreshold() {
            order.startInspection();
            order.clearEvents();

            // 記錄 20 筆結果，1 筆不合格 (5% < 10%)
            for (int i = 0; i < 19; i++) {
                order.recordResult(standard,
                        new MeasuredValue(10.00, "mm", LocalDateTime.now(), "OP-001"));
            }
            order.recordResult(standard, failingValue);
            order.clearEvents();

            order.complete();

            List<DomainEvent> events = order.getDomainEvents();
            assertThat(events).noneMatch(e -> e instanceof QualityAlertEvent);
        }
    }

    @Nested
    @DisplayName("領域事件測試")
    class DomainEventTest {

        @Test
        @DisplayName("開始檢驗應觸發 InspectionOrderCreatedEvent")
        void shouldRaiseCreatedEventOnStart() {
            order.startInspection();

            List<DomainEvent> events = order.getDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(InspectionOrderCreatedEvent.class);

            InspectionOrderCreatedEvent event = (InspectionOrderCreatedEvent) events.get(0);
            assertThat(event.getAggregateId()).isEqualTo("INS-001");
            assertThat(event.getWorkOrderId()).isEqualTo("WO-001");
            assertThat(event.getProductCode()).isEqualTo("PRODUCT-A");
            assertThat(event.getInspectionType()).isEqualTo("FINAL");
        }

        @Test
        @DisplayName("清除事件後應為空")
        void shouldClearEvents() {
            order.startInspection();
            assertThat(order.getDomainEvents()).isNotEmpty();

            order.clearEvents();
            assertThat(order.getDomainEvents()).isEmpty();
        }
    }

    @Nested
    @DisplayName("不良率計算測試")
    class DefectRateTest {

        @Test
        @DisplayName("無結果時不良率應為 0")
        void shouldReturnZeroDefectRateWhenNoResults() {
            assertThat(order.getDefectRate()).isEqualTo(0.0);
        }

        @Test
        @DisplayName("正確計算不良率")
        void shouldCalculateDefectRateCorrectly() {
            order.startInspection();
            order.recordResult(standard, passingValue);
            order.recordResult(standard, failingValue);
            order.recordResult(standard, passingValue);
            order.recordResult(standard, failingValue);

            assertThat(order.getDefectRate()).isEqualTo(0.5);
        }
    }
}
