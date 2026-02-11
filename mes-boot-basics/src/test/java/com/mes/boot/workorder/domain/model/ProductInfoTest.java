package com.mes.boot.workorder.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * [DDD Pattern: Value Object 測試]
 * [SOLID: SRP - 測試 ProductInfo 值物件的驗證邏輯與值相等性]
 *
 * 測試 ProductInfo Value Object 的核心行為：
 * - 建構驗證：不允許空值
 * - 值相等性：所有屬性相同即為相等
 */
@DisplayName("產品資訊值物件 (ProductInfo Value Object)")
class ProductInfoTest {

    @Nested
    @DisplayName("建構驗證")
    class Validation {

        @Test
        @DisplayName("合法的產品資訊應成功建立")
        void shouldCreateValidProductInfo() {
            ProductInfo info = new ProductInfo("WAFER-001", "8吋晶圓", "P型 <100>");

            assertThat(info.getProductCode()).isEqualTo("WAFER-001");
            assertThat(info.getProductName()).isEqualTo("8吋晶圓");
            assertThat(info.getSpecification()).isEqualTo("P型 <100>");
        }

        @Test
        @DisplayName("產品代碼不可為 null")
        void shouldRejectNullProductCode() {
            assertThatThrownBy(() -> new ProductInfo(null, "Name", "Spec"))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Product code must not be null");
        }

        @Test
        @DisplayName("產品名稱不可為 null")
        void shouldRejectNullProductName() {
            assertThatThrownBy(() -> new ProductInfo("CODE", null, "Spec"))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Product name must not be null");
        }

        @Test
        @DisplayName("產品代碼不可為空字串")
        void shouldRejectEmptyProductCode() {
            assertThatThrownBy(() -> new ProductInfo("  ", "Name", "Spec"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Product code must not be empty");
        }

        @Test
        @DisplayName("產品名稱不可為空字串")
        void shouldRejectEmptyProductName() {
            assertThatThrownBy(() -> new ProductInfo("CODE", "  ", "Spec"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Product name must not be empty");
        }

        @Test
        @DisplayName("規格可以為 null（選填欄位）")
        void shouldAllowNullSpecification() {
            ProductInfo info = new ProductInfo("CODE", "Name", null);

            assertThat(info.getSpecification()).isNull();
        }
    }

    @Nested
    @DisplayName("值相等性 (Value Equality)")
    class Equality {

        @Test
        @DisplayName("所有屬性相同的 ProductInfo 應相等")
        void shouldBeEqualWhenAllFieldsSame() {
            ProductInfo info1 = new ProductInfo("WAFER-001", "8吋晶圓", "P型");
            ProductInfo info2 = new ProductInfo("WAFER-001", "8吋晶圓", "P型");

            assertThat(info1).isEqualTo(info2);
            assertThat(info1.hashCode()).isEqualTo(info2.hashCode());
        }

        @Test
        @DisplayName("產品代碼不同的 ProductInfo 應不相等")
        void shouldNotBeEqualWhenProductCodeDiffers() {
            ProductInfo info1 = new ProductInfo("WAFER-001", "8吋晶圓", "P型");
            ProductInfo info2 = new ProductInfo("WAFER-002", "8吋晶圓", "P型");

            assertThat(info1).isNotEqualTo(info2);
        }

        @Test
        @DisplayName("產品名稱不同的 ProductInfo 應不相等")
        void shouldNotBeEqualWhenProductNameDiffers() {
            ProductInfo info1 = new ProductInfo("WAFER-001", "8吋晶圓", "P型");
            ProductInfo info2 = new ProductInfo("WAFER-001", "12吋晶圓", "P型");

            assertThat(info1).isNotEqualTo(info2);
        }

        @Test
        @DisplayName("規格不同的 ProductInfo 應不相等")
        void shouldNotBeEqualWhenSpecificationDiffers() {
            ProductInfo info1 = new ProductInfo("WAFER-001", "8吋晶圓", "P型");
            ProductInfo info2 = new ProductInfo("WAFER-001", "8吋晶圓", "N型");

            assertThat(info1).isNotEqualTo(info2);
        }

        @Test
        @DisplayName("與 null 比較應不相等")
        void shouldNotBeEqualToNull() {
            ProductInfo info = new ProductInfo("WAFER-001", "8吋晶圓", "P型");

            assertThat(info).isNotEqualTo(null);
        }

        @Test
        @DisplayName("與不同型別比較應不相等")
        void shouldNotBeEqualToDifferentType() {
            ProductInfo info = new ProductInfo("WAFER-001", "8吋晶圓", "P型");

            assertThat(info).isNotEqualTo("WAFER-001");
        }
    }
}
