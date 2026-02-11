package com.mes.kafka.quality.application.command;

import com.mes.common.cqrs.Command;

/**
 * [CQRS Pattern: Command - 記錄檢驗結果命令]
 * [SOLID: SRP - 只攜帶記錄檢驗結果所需的資料]
 *
 * 記錄品質檢驗的量測結果命令。
 * 包含品質標準定義與實際量測值。
 */
public class RecordInspectionResultCommand implements Command {

    private final String inspectionOrderId;
    private final String standardCode;
    private final double lowerBound;
    private final double upperBound;
    private final String unit;
    private final double measuredValue;
    private final String measuredUnit;
    private final String inspector;

    public RecordInspectionResultCommand(String inspectionOrderId, String standardCode,
                                         double lowerBound, double upperBound, String unit,
                                         double measuredValue, String measuredUnit, String inspector) {
        this.inspectionOrderId = inspectionOrderId;
        this.standardCode = standardCode;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.unit = unit;
        this.measuredValue = measuredValue;
        this.measuredUnit = measuredUnit;
        this.inspector = inspector;
    }

    public String getInspectionOrderId() {
        return inspectionOrderId;
    }

    public String getStandardCode() {
        return standardCode;
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public double getUpperBound() {
        return upperBound;
    }

    public String getUnit() {
        return unit;
    }

    public double getMeasuredValue() {
        return measuredValue;
    }

    public String getMeasuredUnit() {
        return measuredUnit;
    }

    public String getInspector() {
        return inspector;
    }
}
