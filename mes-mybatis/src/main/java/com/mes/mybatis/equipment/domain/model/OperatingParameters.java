package com.mes.mybatis.equipment.domain.model;

import com.mes.common.ddd.annotation.ValueObject;
import com.mes.common.ddd.model.BaseValueObject;

import java.util.Arrays;
import java.util.List;

/**
 * [DDD Pattern: Value Object - 設備運行參數]
 *
 * 描述設備當前的運行參數（溫度、壓力、速度、振動）。
 * 不可變，如需更新會建立新的 OperatingParameters 實例。
 */
@ValueObject
public class OperatingParameters extends BaseValueObject {

    private final double temperature;
    private final double pressure;
    private final double speed;
    private final double vibration;

    public OperatingParameters(double temperature, double pressure, double speed, double vibration) {
        this.temperature = temperature;
        this.pressure = pressure;
        this.speed = speed;
        this.vibration = vibration;
    }

    public static OperatingParameters defaultParameters() {
        return new OperatingParameters(0.0, 0.0, 0.0, 0.0);
    }

    public double getTemperature() {
        return temperature;
    }

    public double getPressure() {
        return pressure;
    }

    public double getSpeed() {
        return speed;
    }

    public double getVibration() {
        return vibration;
    }

    @Override
    protected List<Object> getEqualityComponents() {
        return Arrays.<Object>asList(temperature, pressure, speed, vibration);
    }

    @Override
    public String toString() {
        return String.format("OperatingParameters{temperature=%.2f, pressure=%.2f, speed=%.2f, vibration=%.2f}",
                temperature, pressure, speed, vibration);
    }
}
