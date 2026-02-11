package com.mes.mybatis.equipment.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Location Value Object 的單元測試。
 * 驗證不可變性與值相等性。
 */
@DisplayName("Location Value Object")
class LocationTest {

    @Test
    @DisplayName("相同屬性的 Location 應該相等")
    void shouldBeEqualWhenSameProperties() {
        Location location1 = new Location("A棟", "1", "加工區", "A1-01");
        Location location2 = new Location("A棟", "1", "加工區", "A1-01");

        assertThat(location1).isEqualTo(location2);
        assertThat(location1.hashCode()).isEqualTo(location2.hashCode());
    }

    @Test
    @DisplayName("不同屬性的 Location 不應該相等")
    void shouldNotBeEqualWhenDifferentProperties() {
        Location location1 = new Location("A棟", "1", "加工區", "A1-01");
        Location location2 = new Location("B棟", "1", "加工區", "A1-01");

        assertThat(location1).isNotEqualTo(location2);
    }

    @Test
    @DisplayName("任一欄位不同都應視為不相等")
    void shouldNotBeEqualWhenAnyFieldDiffers() {
        Location base = new Location("A棟", "1", "加工區", "A1-01");

        assertThat(base).isNotEqualTo(new Location("B棟", "1", "加工區", "A1-01"));
        assertThat(base).isNotEqualTo(new Location("A棟", "2", "加工區", "A1-01"));
        assertThat(base).isNotEqualTo(new Location("A棟", "1", "組裝區", "A1-01"));
        assertThat(base).isNotEqualTo(new Location("A棟", "1", "加工區", "A1-02"));
    }

    @Test
    @DisplayName("building 不可為 null")
    void shouldRejectNullBuilding() {
        assertThatThrownBy(() -> new Location(null, "1", "加工區", "A1-01"))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Building");
    }

    @Test
    @DisplayName("floor 不可為 null")
    void shouldRejectNullFloor() {
        assertThatThrownBy(() -> new Location("A棟", null, "加工區", "A1-01"))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Floor");
    }

    @Test
    @DisplayName("zone 不可為 null")
    void shouldRejectNullZone() {
        assertThatThrownBy(() -> new Location("A棟", "1", null, "A1-01"))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Zone");
    }

    @Test
    @DisplayName("position 不可為 null")
    void shouldRejectNullPosition() {
        assertThatThrownBy(() -> new Location("A棟", "1", "加工區", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Position");
    }

    @Test
    @DisplayName("getter 應回傳正確的值")
    void shouldReturnCorrectValues() {
        Location location = new Location("A棟", "1", "加工區", "A1-01");

        assertThat(location.getBuilding()).isEqualTo("A棟");
        assertThat(location.getFloor()).isEqualTo("1");
        assertThat(location.getZone()).isEqualTo("加工區");
        assertThat(location.getPosition()).isEqualTo("A1-01");
    }

    @Test
    @DisplayName("toString 應包含所有屬性")
    void shouldHaveReadableToString() {
        Location location = new Location("A棟", "1", "加工區", "A1-01");
        String str = location.toString();

        assertThat(str).contains("A棟");
        assertThat(str).contains("1");
        assertThat(str).contains("加工區");
        assertThat(str).contains("A1-01");
    }
}
