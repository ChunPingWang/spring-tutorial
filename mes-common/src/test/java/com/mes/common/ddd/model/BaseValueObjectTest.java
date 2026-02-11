package com.mes.common.ddd.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BaseValueObject - Value Object 相等性測試")
class BaseValueObjectTest {

    @Test
    @DisplayName("相同屬性值的 Value Object 應相等")
    void twoValueObjectsWithSameFields_shouldBeEqual() {
        TestVO vo1 = new TestVO("A", 100);
        TestVO vo2 = new TestVO("A", 100);

        assertThat(vo1).isEqualTo(vo2);
        assertThat(vo1.hashCode()).isEqualTo(vo2.hashCode());
    }

    @Test
    @DisplayName("不同屬性值的 Value Object 不應相等")
    void twoValueObjectsWithDifferentFields_shouldNotBeEqual() {
        TestVO vo1 = new TestVO("A", 100);
        TestVO vo2 = new TestVO("B", 200);

        assertThat(vo1).isNotEqualTo(vo2);
    }

    @Test
    @DisplayName("部分屬性不同的 Value Object 不應相等")
    void partiallyDifferentFields_shouldNotBeEqual() {
        TestVO vo1 = new TestVO("A", 100);
        TestVO vo2 = new TestVO("A", 200);

        assertThat(vo1).isNotEqualTo(vo2);
    }

    @Test
    @DisplayName("與 null 比較應不相等")
    void compareWithNull_shouldNotBeEqual() {
        TestVO vo = new TestVO("A", 100);

        assertThat(vo).isNotEqualTo(null);
    }

    @Test
    @DisplayName("與不同型別比較應不相等")
    void compareWithDifferentType_shouldNotBeEqual() {
        TestVO vo = new TestVO("A", 100);

        assertThat(vo).isNotEqualTo("not a value object");
    }

    @Test
    @DisplayName("toString 應包含類別名稱和屬性")
    void toString_shouldIncludeClassNameAndComponents() {
        TestVO vo = new TestVO("A", 100);
        String str = vo.toString();

        assertThat(str).contains("TestVO");
        assertThat(str).contains("A");
        assertThat(str).contains("100");
    }

    // -- Test double --

    static class TestVO extends BaseValueObject {
        private final String name;
        private final int value;

        TestVO(String name, int value) {
            this.name = name;
            this.value = value;
        }

        @Override
        protected List<Object> getEqualityComponents() {
            return Arrays.asList(name, value);
        }
    }
}
