package com.mes.web.production.domain.model;

import com.mes.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * [DDD Pattern: Value Object 單元測試]
 *
 * 測試 OutputQuantity 值物件的：
 * 1. 自驗證邏輯（負數拒絕）
 * 2. 總產出計算
 * 3. 良率計算
 * 4. 值相等性
 */
@DisplayName("OutputQuantity 值物件測試")
class OutputQuantityTest {

    @Nested
    @DisplayName("自驗證測試")
    class ValidationTests {

        @Test
        @DisplayName("良品數量不可為負數")
        void shouldRejectNegativeGood() {
            assertThatThrownBy(() -> new OutputQuantity(-1, 0, 0))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("良品數量不可為負數");
        }

        @Test
        @DisplayName("不良品數量不可為負數")
        void shouldRejectNegativeDefective() {
            assertThatThrownBy(() -> new OutputQuantity(0, -1, 0))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("不良品數量不可為負數");
        }

        @Test
        @DisplayName("重工品數量不可為負數")
        void shouldRejectNegativeRework() {
            assertThatThrownBy(() -> new OutputQuantity(0, 0, -1))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("重工品數量不可為負數");
        }

        @Test
        @DisplayName("所有數量為零時應可建立")
        void shouldAcceptZeroValues() {
            OutputQuantity output = new OutputQuantity(0, 0, 0);
            assertThat(output.getGood()).isEqualTo(0);
            assertThat(output.getDefective()).isEqualTo(0);
            assertThat(output.getRework()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("總產出計算測試")
    class TotalTests {

        @Test
        @DisplayName("總產出 = 良品 + 不良品 + 重工品")
        void shouldCalculateTotal() {
            OutputQuantity output = new OutputQuantity(100, 10, 5);
            assertThat(output.getTotal()).isEqualTo(115);
        }

        @Test
        @DisplayName("零產出的總數應為零")
        void shouldReturnZeroForEmptyOutput() {
            OutputQuantity output = OutputQuantity.zero();
            assertThat(output.getTotal()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("良率計算測試")
    class YieldRateTests {

        @Test
        @DisplayName("100% 良率 - 全部為良品")
        void shouldCalculateFullYieldRate() {
            OutputQuantity output = new OutputQuantity(100, 0, 0);
            assertThat(output.getYieldRate()).isEqualByComparingTo(new BigDecimal("100.00"));
        }

        @Test
        @DisplayName("正常良率計算 - 90/100 = 90%")
        void shouldCalculateNormalYieldRate() {
            OutputQuantity output = new OutputQuantity(90, 10, 0);
            assertThat(output.getYieldRate()).isEqualByComparingTo(new BigDecimal("90.00"));
        }

        @Test
        @DisplayName("含重工品的良率計算 - 80/100 = 80%")
        void shouldCalculateYieldRateWithRework() {
            OutputQuantity output = new OutputQuantity(80, 10, 10);
            assertThat(output.getYieldRate()).isEqualByComparingTo(new BigDecimal("80.00"));
        }

        @Test
        @DisplayName("零產出的良率應為零")
        void shouldReturnZeroYieldRateForEmptyOutput() {
            OutputQuantity output = OutputQuantity.zero();
            assertThat(output.getYieldRate()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("非整除的良率應正確四捨五入 - 1/3 = 33.33%")
        void shouldRoundYieldRate() {
            OutputQuantity output = new OutputQuantity(1, 1, 1);
            assertThat(output.getYieldRate()).isEqualByComparingTo(new BigDecimal("33.33"));
        }
    }

    @Nested
    @DisplayName("值相等性測試")
    class EqualityTests {

        @Test
        @DisplayName("相同屬性的 OutputQuantity 應相等")
        void shouldBeEqualWithSameValues() {
            OutputQuantity output1 = new OutputQuantity(100, 10, 5);
            OutputQuantity output2 = new OutputQuantity(100, 10, 5);

            assertThat(output1).isEqualTo(output2);
            assertThat(output1.hashCode()).isEqualTo(output2.hashCode());
        }

        @Test
        @DisplayName("不同屬性的 OutputQuantity 不應相等")
        void shouldNotBeEqualWithDifferentValues() {
            OutputQuantity output1 = new OutputQuantity(100, 10, 5);
            OutputQuantity output2 = new OutputQuantity(90, 10, 5);

            assertThat(output1).isNotEqualTo(output2);
        }
    }
}
