package com.mes.kafka.quality.domain.service;

import com.mes.kafka.quality.domain.model.InspectionResult;
import com.mes.kafka.quality.domain.model.InspectionResultId;
import com.mes.kafka.quality.domain.model.MeasuredValue;
import com.mes.kafka.quality.domain.model.QualityStandard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * QualityDomainService 領域服務單元測試。
 * 驗證不良率計算與 SPC 分析邏輯。
 */
@DisplayName("QualityDomainService 領域服務測試")
class QualityDomainServiceTest {

    private QualityDomainService service;
    private QualityStandard standard;

    @BeforeEach
    void setUp() {
        service = new QualityDomainService();
        standard = new QualityStandard("DIM-001", 9.95, 10.05, "mm");
    }

    @Nested
    @DisplayName("不良率計算測試")
    class CalculateDefectRateTest {

        @Test
        @DisplayName("空列表應回傳 0.0")
        void shouldReturnZeroForEmptyList() {
            assertThat(service.calculateDefectRate(Collections.<InspectionResult>emptyList())).isEqualTo(0.0);
        }

        @Test
        @DisplayName("null 列表應回傳 0.0")
        void shouldReturnZeroForNull() {
            assertThat(service.calculateDefectRate(null)).isEqualTo(0.0);
        }

        @Test
        @DisplayName("全部合格應回傳 0.0")
        void shouldReturnZeroWhenAllPassed() {
            List<InspectionResult> results = Arrays.asList(
                    createResult("RES-001", 10.00),
                    createResult("RES-002", 10.01),
                    createResult("RES-003", 9.99));

            assertThat(service.calculateDefectRate(results)).isEqualTo(0.0);
        }

        @Test
        @DisplayName("全部不合格應回傳 1.0")
        void shouldReturnOneWhenAllFailed() {
            List<InspectionResult> results = Arrays.asList(
                    createResult("RES-001", 10.10),
                    createResult("RES-002", 9.90));

            assertThat(service.calculateDefectRate(results)).isEqualTo(1.0);
        }

        @Test
        @DisplayName("正確計算混合結果的不良率")
        void shouldCalculateCorrectRate() {
            List<InspectionResult> results = Arrays.asList(
                    createResult("RES-001", 10.00),  // pass
                    createResult("RES-002", 10.10),  // fail
                    createResult("RES-003", 10.01),  // pass
                    createResult("RES-004", 9.90));   // fail

            assertThat(service.calculateDefectRate(results)).isEqualTo(0.5);
        }
    }

    @Nested
    @DisplayName("SPC 分析測試 - 連續 7 點同側規則")
    class IsWithinSPCTest {

        @Test
        @DisplayName("少於 7 個量測值應回傳 true（資料不足）")
        void shouldReturnTrueWhenLessThan7Values() {
            List<MeasuredValue> values = Arrays.asList(
                    createMeasuredValue(10.01),
                    createMeasuredValue(10.02),
                    createMeasuredValue(10.01));

            assertThat(service.isWithinSPC(values, standard)).isTrue();
        }

        @Test
        @DisplayName("null 列表應回傳 true")
        void shouldReturnTrueForNull() {
            assertThat(service.isWithinSPC(null, standard)).isTrue();
        }

        @Test
        @DisplayName("正常波動的量測值應回傳 true")
        void shouldReturnTrueForNormalVariation() {
            // 標準中心值為 10.00，值在兩側交替
            List<MeasuredValue> values = Arrays.asList(
                    createMeasuredValue(10.01),
                    createMeasuredValue(9.99),
                    createMeasuredValue(10.02),
                    createMeasuredValue(9.98),
                    createMeasuredValue(10.01),
                    createMeasuredValue(9.99),
                    createMeasuredValue(10.01));

            assertThat(service.isWithinSPC(values, standard)).isTrue();
        }

        @Test
        @DisplayName("連續 7 個值在平均值上方應回傳 false（偵測到趨勢）")
        void shouldReturnFalseWhenSevenConsecutiveAboveMean() {
            // 標準中心值為 10.00，連續 7 個值都大於 10.00
            List<MeasuredValue> values = Arrays.asList(
                    createMeasuredValue(10.01),
                    createMeasuredValue(10.02),
                    createMeasuredValue(10.01),
                    createMeasuredValue(10.03),
                    createMeasuredValue(10.01),
                    createMeasuredValue(10.02),
                    createMeasuredValue(10.01));

            assertThat(service.isWithinSPC(values, standard)).isFalse();
        }

        @Test
        @DisplayName("連續 7 個值在平均值下方應回傳 false（偵測到趨勢）")
        void shouldReturnFalseWhenSevenConsecutiveBelowMean() {
            // 標準中心值為 10.00，連續 7 個值都小於 10.00
            List<MeasuredValue> values = Arrays.asList(
                    createMeasuredValue(9.99),
                    createMeasuredValue(9.98),
                    createMeasuredValue(9.99),
                    createMeasuredValue(9.97),
                    createMeasuredValue(9.99),
                    createMeasuredValue(9.98),
                    createMeasuredValue(9.99));

            assertThat(service.isWithinSPC(values, standard)).isFalse();
        }

        @Test
        @DisplayName("恰好 6 個連續同側值應回傳 true")
        void shouldReturnTrueWhenOnlySixConsecutive() {
            List<MeasuredValue> values = Arrays.asList(
                    createMeasuredValue(10.01),
                    createMeasuredValue(10.02),
                    createMeasuredValue(10.01),
                    createMeasuredValue(10.03),
                    createMeasuredValue(10.01),
                    createMeasuredValue(10.02),
                    createMeasuredValue(9.99));  // 第 7 個在另一側

            assertThat(service.isWithinSPC(values, standard)).isTrue();
        }

        @Test
        @DisplayName("中間出現連續 7 個同側值應回傳 false")
        void shouldDetectTrendInMiddle() {
            List<MeasuredValue> values = new ArrayList<>();
            // 前 3 個正常
            values.add(createMeasuredValue(9.99));
            values.add(createMeasuredValue(10.01));
            values.add(createMeasuredValue(9.98));
            // 連續 7 個在上方
            for (int i = 0; i < 7; i++) {
                values.add(createMeasuredValue(10.01 + i * 0.001));
            }

            assertThat(service.isWithinSPC(values, standard)).isFalse();
        }
    }

    private InspectionResult createResult(String id, double measuredValue) {
        MeasuredValue mv = new MeasuredValue(measuredValue, "mm", LocalDateTime.now(), "OP-001");
        return new InspectionResult(new InspectionResultId(id), standard, mv);
    }

    private MeasuredValue createMeasuredValue(double value) {
        return new MeasuredValue(value, "mm", LocalDateTime.now(), "OP-001");
    }
}
