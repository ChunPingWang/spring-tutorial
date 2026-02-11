package com.mes.kafka.quality.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MeasuredValue 值物件單元測試。
 * 驗證量測值與品質標準比對邏輯。
 */
@DisplayName("MeasuredValue 值物件測試")
class MeasuredValueTest {

    private final QualityStandard standard = new QualityStandard("DIM-001", 9.95, 10.05, "mm");

    @Test
    @DisplayName("量測值在標準範圍內應判定為合格")
    void shouldBeWithinStandardWhenValueInRange() {
        MeasuredValue value = new MeasuredValue(10.00, "mm", LocalDateTime.now(), "OP-001");
        assertThat(value.isWithinStandard(standard)).isTrue();
    }

    @Test
    @DisplayName("量測值等於下限應判定為合格")
    void shouldBeWithinStandardWhenValueEqualsLowerBound() {
        MeasuredValue value = new MeasuredValue(9.95, "mm", LocalDateTime.now(), "OP-001");
        assertThat(value.isWithinStandard(standard)).isTrue();
    }

    @Test
    @DisplayName("量測值等於上限應判定為合格")
    void shouldBeWithinStandardWhenValueEqualsUpperBound() {
        MeasuredValue value = new MeasuredValue(10.05, "mm", LocalDateTime.now(), "OP-001");
        assertThat(value.isWithinStandard(standard)).isTrue();
    }

    @Test
    @DisplayName("量測值低於下限應判定為不合格")
    void shouldNotBeWithinStandardWhenValueBelowLowerBound() {
        MeasuredValue value = new MeasuredValue(9.94, "mm", LocalDateTime.now(), "OP-001");
        assertThat(value.isWithinStandard(standard)).isFalse();
    }

    @Test
    @DisplayName("量測值高於上限應判定為不合格")
    void shouldNotBeWithinStandardWhenValueAboveUpperBound() {
        MeasuredValue value = new MeasuredValue(10.06, "mm", LocalDateTime.now(), "OP-001");
        assertThat(value.isWithinStandard(standard)).isFalse();
    }

    @Test
    @DisplayName("相同值的 MeasuredValue 應相等")
    void shouldBeEqualWhenValuesAreSame() {
        LocalDateTime now = LocalDateTime.now();
        MeasuredValue value1 = new MeasuredValue(10.00, "mm", now, "OP-001");
        MeasuredValue value2 = new MeasuredValue(10.00, "mm", now, "OP-001");
        assertThat(value1).isEqualTo(value2);
    }

    @Test
    @DisplayName("不同值的 MeasuredValue 應不相等")
    void shouldNotBeEqualWhenValuesDiffer() {
        LocalDateTime now = LocalDateTime.now();
        MeasuredValue value1 = new MeasuredValue(10.00, "mm", now, "OP-001");
        MeasuredValue value2 = new MeasuredValue(10.01, "mm", now, "OP-001");
        assertThat(value1).isNotEqualTo(value2);
    }

    @Test
    @DisplayName("應正確回傳屬性值")
    void shouldReturnCorrectProperties() {
        LocalDateTime now = LocalDateTime.now();
        MeasuredValue value = new MeasuredValue(10.02, "mm", now, "OP-002");

        assertThat(value.getValue()).isEqualTo(10.02);
        assertThat(value.getUnit()).isEqualTo("mm");
        assertThat(value.getMeasuredAt()).isEqualTo(now);
        assertThat(value.getInspector()).isEqualTo("OP-002");
    }
}
