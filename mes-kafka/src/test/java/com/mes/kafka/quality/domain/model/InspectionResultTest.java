package com.mes.kafka.quality.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * InspectionResult Entity 單元測試。
 * 驗證自動判定合格/不合格的邏輯。
 */
@DisplayName("InspectionResult Entity 測試")
class InspectionResultTest {

    private final QualityStandard standard = new QualityStandard("DIM-001", 9.95, 10.05, "mm");

    @Test
    @DisplayName("量測值在標準內應自動判定為合格")
    void shouldAutoPassWhenValueWithinStandard() {
        MeasuredValue value = new MeasuredValue(10.00, "mm", LocalDateTime.now(), "OP-001");
        InspectionResult result = new InspectionResult(
                new InspectionResultId("RES-001"), standard, value);

        assertThat(result.isPassed()).isTrue();
        assertThat(result.getDefectDetail()).isNull();
    }

    @Test
    @DisplayName("量測值超出標準應自動判定為不合格")
    void shouldAutoFailWhenValueOutsideStandard() {
        MeasuredValue value = new MeasuredValue(10.10, "mm", LocalDateTime.now(), "OP-001");
        InspectionResult result = new InspectionResult(
                new InspectionResultId("RES-002"), standard, value);

        assertThat(result.isPassed()).isFalse();
    }

    @Test
    @DisplayName("帶缺陷詳情的不合格結果")
    void shouldContainDefectDetailWhenProvided() {
        MeasuredValue value = new MeasuredValue(10.10, "mm", LocalDateTime.now(), "OP-001");
        DefectDetail defect = new DefectDetail("DEF-001", "尺寸偏差", "MAJOR", "超出上限 0.05mm");
        InspectionResult result = new InspectionResult(
                new InspectionResultId("RES-003"), standard, value, defect);

        assertThat(result.isPassed()).isFalse();
        assertThat(result.getDefectDetail()).isNotNull();
        assertThat(result.getDefectDetail().getDefectCode()).isEqualTo("DEF-001");
        assertThat(result.getDefectDetail().getSeverity()).isEqualTo("MAJOR");
    }

    @Test
    @DisplayName("合格結果也可以附帶缺陷詳情（如外觀輕微缺陷但仍在規格內）")
    void shouldAllowDefectDetailEvenWhenPassed() {
        MeasuredValue value = new MeasuredValue(10.00, "mm", LocalDateTime.now(), "OP-001");
        DefectDetail defect = new DefectDetail("DEF-002", "外觀", "MINOR", "輕微刮痕但在規格內");
        InspectionResult result = new InspectionResult(
                new InspectionResultId("RES-004"), standard, value, defect);

        assertThat(result.isPassed()).isTrue();
        assertThat(result.getDefectDetail()).isNotNull();
    }

    @Test
    @DisplayName("應正確回傳標準與量測值")
    void shouldReturnCorrectStandardAndMeasuredValue() {
        MeasuredValue value = new MeasuredValue(10.00, "mm", LocalDateTime.now(), "OP-001");
        InspectionResult result = new InspectionResult(
                new InspectionResultId("RES-005"), standard, value);

        assertThat(result.getStandard()).isEqualTo(standard);
        assertThat(result.getMeasuredValue()).isEqualTo(value);
        assertThat(result.getId().getValue()).isEqualTo("RES-005");
    }

    @Test
    @DisplayName("邊界值測試 - 恰好等於下限")
    void shouldPassWhenValueExactlyAtLowerBound() {
        MeasuredValue value = new MeasuredValue(9.95, "mm", LocalDateTime.now(), "OP-001");
        InspectionResult result = new InspectionResult(
                new InspectionResultId("RES-006"), standard, value);

        assertThat(result.isPassed()).isTrue();
    }

    @Test
    @DisplayName("邊界值測試 - 恰好等於上限")
    void shouldPassWhenValueExactlyAtUpperBound() {
        MeasuredValue value = new MeasuredValue(10.05, "mm", LocalDateTime.now(), "OP-001");
        InspectionResult result = new InspectionResult(
                new InspectionResultId("RES-007"), standard, value);

        assertThat(result.isPassed()).isTrue();
    }
}
