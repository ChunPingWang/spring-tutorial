package com.mes.boot.workorder.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

/**
 * [DDD Pattern: Value Object 測試]
 * [SOLID: SRP - 測試 Quantity 值物件的自驗證邏輯與良率計算]
 *
 * 測試 Quantity Value Object 的核心行為：
 * - 自驗證（Self-Validation）：建構時拒絕不合法的值
 * - 良率計算（Yield Rate）
 * - 值相等性（Value Equality）
 */
@DisplayName("數量值物件 (Quantity Value Object)")
class QuantityTest {

    @Nested
    @DisplayName("自驗證 (Self-Validation)")
    class Validation {

        @Test
        @DisplayName("合法的數量應成功建立")
        void shouldCreateValidQuantity() {
            Quantity quantity = new Quantity(1000, 950, 20);

            assertThat(quantity.getPlanned()).isEqualTo(1000);
            assertThat(quantity.getCompleted()).isEqualTo(950);
            assertThat(quantity.getDefective()).isEqualTo(20);
        }

        @Test
        @DisplayName("計畫量不可為負數")
        void shouldRejectNegativePlanned() {
            assertThatThrownBy(() -> new Quantity(-1, 0, 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Planned quantity must not be negative");
        }

        @Test
        @DisplayName("完成量不可為負數")
        void shouldRejectNegativeCompleted() {
            assertThatThrownBy(() -> new Quantity(100, -1, 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Completed quantity must not be negative");
        }

        @Test
        @DisplayName("不良量不可為負數")
        void shouldRejectNegativeDefective() {
            assertThatThrownBy(() -> new Quantity(100, 50, -1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Defective quantity must not be negative");
        }

        @Test
        @DisplayName("不良量不可超過計畫量")
        void shouldRejectDefectiveExceedingPlanned() {
            assertThatThrownBy(() -> new Quantity(100, 50, 101))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("must not exceed planned quantity");
        }

        @Test
        @DisplayName("不良量等於計畫量應允許（全部不良）")
        void shouldAllowDefectiveEqualToPlanned() {
            Quantity quantity = new Quantity(100, 100, 100);

            assertThat(quantity.getDefective()).isEqualTo(100);
        }

        @Test
        @DisplayName("所有數量為零應允許")
        void shouldAllowAllZeros() {
            Quantity quantity = new Quantity(0, 0, 0);

            assertThat(quantity.getPlanned()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("良率計算 (Yield Rate)")
    class YieldRate {

        @Test
        @DisplayName("無不良品時良率應為 1.0")
        void shouldReturnFullYieldWhenNoDefects() {
            Quantity quantity = new Quantity(1000, 1000, 0);

            assertThat(quantity.getYieldRate()).isCloseTo(1.0, within(0.001));
        }

        @Test
        @DisplayName("有不良品時應正確計算良率")
        void shouldCalculateYieldRateCorrectly() {
            // (1000 - 50) / 1000.0 = 0.95
            Quantity quantity = new Quantity(1000, 950, 50);

            assertThat(quantity.getYieldRate()).isCloseTo(0.95, within(0.001));
        }

        @Test
        @DisplayName("全部不良時良率應為 0.0")
        void shouldReturnZeroYieldWhenAllDefective() {
            Quantity quantity = new Quantity(100, 0, 100);

            assertThat(quantity.getYieldRate()).isCloseTo(0.0, within(0.001));
        }

        @Test
        @DisplayName("計畫量為零時良率應為 0.0（避免除以零）")
        void shouldReturnZeroYieldWhenPlannedIsZero() {
            Quantity quantity = new Quantity(0, 0, 0);

            assertThat(quantity.getYieldRate()).isCloseTo(0.0, within(0.001));
        }
    }

    @Nested
    @DisplayName("工廠方法")
    class FactoryMethods {

        @Test
        @DisplayName("ofPlanned 應建立只有計畫量的數量")
        void shouldCreateQuantityWithOnlyPlanned() {
            Quantity quantity = Quantity.ofPlanned(500);

            assertThat(quantity.getPlanned()).isEqualTo(500);
            assertThat(quantity.getCompleted()).isEqualTo(0);
            assertThat(quantity.getDefective()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("值相等性 (Value Equality)")
    class Equality {

        @Test
        @DisplayName("相同數值的 Quantity 應相等")
        void shouldBeEqualWhenSameValues() {
            Quantity q1 = new Quantity(100, 80, 5);
            Quantity q2 = new Quantity(100, 80, 5);

            assertThat(q1).isEqualTo(q2);
            assertThat(q1.hashCode()).isEqualTo(q2.hashCode());
        }

        @Test
        @DisplayName("不同數值的 Quantity 應不相等")
        void shouldNotBeEqualWhenDifferentValues() {
            Quantity q1 = new Quantity(100, 80, 5);
            Quantity q2 = new Quantity(100, 80, 10);

            assertThat(q1).isNotEqualTo(q2);
        }
    }
}
