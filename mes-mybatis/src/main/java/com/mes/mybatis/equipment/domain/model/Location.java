package com.mes.mybatis.equipment.domain.model;

import com.mes.common.ddd.annotation.ValueObject;
import com.mes.common.ddd.model.BaseValueObject;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * [DDD Pattern: Value Object - 設備位置]
 *
 * 描述設備在工廠中的物理位置。
 * 不可變，相等性由所有屬性值共同決定。
 */
@ValueObject
public class Location extends BaseValueObject {

    private final String building;
    private final String floor;
    private final String zone;
    private final String position;

    public Location(String building, String floor, String zone, String position) {
        this.building = Objects.requireNonNull(building, "Building must not be null");
        this.floor = Objects.requireNonNull(floor, "Floor must not be null");
        this.zone = Objects.requireNonNull(zone, "Zone must not be null");
        this.position = Objects.requireNonNull(position, "Position must not be null");
    }

    public String getBuilding() {
        return building;
    }

    public String getFloor() {
        return floor;
    }

    public String getZone() {
        return zone;
    }

    public String getPosition() {
        return position;
    }

    @Override
    protected List<Object> getEqualityComponents() {
        return Arrays.asList(building, floor, zone, position);
    }

    @Override
    public String toString() {
        return String.format("Location{building='%s', floor='%s', zone='%s', position='%s'}",
                building, floor, zone, position);
    }
}
