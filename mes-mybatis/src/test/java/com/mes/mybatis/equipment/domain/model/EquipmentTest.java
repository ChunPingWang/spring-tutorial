package com.mes.mybatis.equipment.domain.model;

import com.mes.common.ddd.event.DomainEvent;
import com.mes.common.exception.BusinessRuleViolationException;
import com.mes.common.exception.DomainException;
import com.mes.mybatis.equipment.domain.event.EquipmentBreakdownEvent;
import com.mes.mybatis.equipment.domain.event.MaintenanceCompletedEvent;
import com.mes.mybatis.equipment.domain.event.MaintenanceScheduledEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Equipment 聚合根的單元測試。
 * 測試狀態轉換、業務規則、領域事件等核心行為。
 */
@DisplayName("Equipment 聚合根")
class EquipmentTest {

    private Equipment equipment;

    @BeforeEach
    void setUp() {
        EquipmentId id = EquipmentId.of("EQ-TEST-001");
        Location location = new Location("A棟", "1", "加工區", "A1-01");
        equipment = new Equipment(id, "測試設備", EquipmentType.CNC, location);
    }

    @Nested
    @DisplayName("初始狀態")
    class InitialState {

        @Test
        @DisplayName("新建設備應該處於 IDLE 狀態")
        void shouldBeIdleWhenCreated() {
            assertThat(equipment.getStatus()).isEqualTo(EquipmentStatus.IDLE);
        }

        @Test
        @DisplayName("新建設備的維護記錄應為空")
        void shouldHaveNoMaintenanceRecords() {
            assertThat(equipment.getMaintenanceRecords()).isEmpty();
        }

        @Test
        @DisplayName("新建設備的運行參數應為預設值")
        void shouldHaveDefaultParameters() {
            OperatingParameters params = equipment.getOperatingParameters();
            assertThat(params.getTemperature()).isEqualTo(0.0);
            assertThat(params.getPressure()).isEqualTo(0.0);
            assertThat(params.getSpeed()).isEqualTo(0.0);
            assertThat(params.getVibration()).isEqualTo(0.0);
        }
    }

    @Nested
    @DisplayName("狀態轉換 - 啟動/停止")
    class StartStopTransitions {

        @Test
        @DisplayName("IDLE 狀態可以啟動運行")
        void shouldStartFromIdle() {
            equipment.startRunning();
            assertThat(equipment.getStatus()).isEqualTo(EquipmentStatus.RUNNING);
        }

        @Test
        @DisplayName("RUNNING 狀態可以停止運行")
        void shouldStopFromRunning() {
            equipment.startRunning();
            equipment.stopRunning();
            assertThat(equipment.getStatus()).isEqualTo(EquipmentStatus.IDLE);
        }

        @Test
        @DisplayName("非 IDLE 狀態不能啟動，應拋出例外")
        void shouldNotStartFromNonIdle() {
            equipment.startRunning(); // 變成 RUNNING
            assertThatThrownBy(() -> equipment.startRunning())
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("expected IDLE");
        }

        @Test
        @DisplayName("非 RUNNING 狀態不能停止，應拋出例外")
        void shouldNotStopFromNonRunning() {
            assertThatThrownBy(() -> equipment.stopRunning())
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("expected RUNNING");
        }
    }

    @Nested
    @DisplayName("故障回報")
    class BreakdownReport {

        @Test
        @DisplayName("回報故障應將狀態設為 BREAKDOWN")
        void shouldSetStatusToBreakdown() {
            equipment.reportBreakdown("主軸異常振動");
            assertThat(equipment.getStatus()).isEqualTo(EquipmentStatus.BREAKDOWN);
        }

        @Test
        @DisplayName("回報故障應註冊 EquipmentBreakdownEvent")
        void shouldRaiseBreakdownEvent() {
            equipment.reportBreakdown("主軸異常振動");

            List<DomainEvent> events = equipment.getDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(EquipmentBreakdownEvent.class);

            EquipmentBreakdownEvent event = (EquipmentBreakdownEvent) events.get(0);
            assertThat(event.getAggregateId()).isEqualTo("EQ-TEST-001");
            assertThat(event.getDescription()).isEqualTo("主軸異常振動");
        }

        @Test
        @DisplayName("已報廢設備不能回報故障")
        void shouldNotReportBreakdownForDecommissioned() {
            equipment.decommission();
            assertThatThrownBy(() -> equipment.reportBreakdown("異常"))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("decommissioned");
        }
    }

    @Nested
    @DisplayName("維護排程")
    class MaintenanceScheduling {

        @Test
        @DisplayName("安排維護應新增維護記錄並設狀態為 MAINTENANCE")
        void shouldScheduleMaintenance() {
            LocalDate scheduledDate = LocalDate.now().plusDays(7);
            equipment.scheduleMaintenance("定期保養", scheduledDate);

            assertThat(equipment.getStatus()).isEqualTo(EquipmentStatus.MAINTENANCE);
            assertThat(equipment.getMaintenanceRecords()).hasSize(1);

            MaintenanceRecord record = equipment.getMaintenanceRecords().get(0);
            assertThat(record.getDescription()).isEqualTo("定期保養");
            assertThat(record.getScheduledDate()).isEqualTo(scheduledDate);
            assertThat(record.getStatus()).isEqualTo("SCHEDULED");
        }

        @Test
        @DisplayName("安排維護應註冊 MaintenanceScheduledEvent")
        void shouldRaiseMaintenanceScheduledEvent() {
            equipment.scheduleMaintenance("定期保養", LocalDate.now().plusDays(7));

            List<DomainEvent> events = equipment.getDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(MaintenanceScheduledEvent.class);
        }

        @Test
        @DisplayName("已報廢設備不能安排維護")
        void shouldNotScheduleForDecommissioned() {
            equipment.decommission();
            assertThatThrownBy(() -> equipment.scheduleMaintenance("保養", LocalDate.now()))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("decommissioned");
        }

        @Test
        @DisplayName("完成維護應更新記錄並將狀態設為 IDLE")
        void shouldCompleteMaintenance() {
            LocalDate scheduledDate = LocalDate.now().plusDays(7);
            equipment.scheduleMaintenance("定期保養", scheduledDate);

            MaintenanceRecordId recordId = equipment.getMaintenanceRecords().get(0).getId();
            equipment.completeMaintenance(recordId, "張技師");

            assertThat(equipment.getStatus()).isEqualTo(EquipmentStatus.IDLE);
            MaintenanceRecord record = equipment.getMaintenanceRecords().get(0);
            assertThat(record.getStatus()).isEqualTo("COMPLETED");
            assertThat(record.getTechnicianName()).isEqualTo("張技師");
            assertThat(record.getCompletedDate()).isNotNull();
        }

        @Test
        @DisplayName("完成維護應註冊 MaintenanceCompletedEvent")
        void shouldRaiseMaintenanceCompletedEvent() {
            equipment.scheduleMaintenance("定期保養", LocalDate.now().plusDays(7));
            equipment.clearEvents(); // 清除排程事件

            MaintenanceRecordId recordId = equipment.getMaintenanceRecords().get(0).getId();
            equipment.completeMaintenance(recordId, "張技師");

            List<DomainEvent> events = equipment.getDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(MaintenanceCompletedEvent.class);

            MaintenanceCompletedEvent event = (MaintenanceCompletedEvent) events.get(0);
            assertThat(event.getTechnicianName()).isEqualTo("張技師");
        }
    }

    @Nested
    @DisplayName("報廢")
    class Decommission {

        @Test
        @DisplayName("報廢設備應將狀態設為 DECOMMISSIONED")
        void shouldDecommission() {
            equipment.decommission();
            assertThat(equipment.getStatus()).isEqualTo(EquipmentStatus.DECOMMISSIONED);
        }

        @Test
        @DisplayName("已報廢設備不能重複報廢")
        void shouldNotDecommissionTwice() {
            equipment.decommission();
            assertThatThrownBy(() -> equipment.decommission())
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("already decommissioned");
        }
    }

    @Nested
    @DisplayName("更新運行參數")
    class UpdateParameters {

        @Test
        @DisplayName("應該可以更新運行參數")
        void shouldUpdateParameters() {
            OperatingParameters newParams = new OperatingParameters(50.0, 3.0, 2000.0, 0.1);
            equipment.updateParameters(newParams);
            assertThat(equipment.getOperatingParameters()).isEqualTo(newParams);
        }
    }
}
