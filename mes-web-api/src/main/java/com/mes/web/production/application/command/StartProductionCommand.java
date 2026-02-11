package com.mes.web.production.application.command;

import com.mes.common.cqrs.Command;

import javax.validation.constraints.NotBlank;

/**
 * [CQRS Pattern: Command - 啟動生產]
 * [SOLID: SRP - 只封裝啟動生產所需的資料]
 *
 * 代表「啟動一筆新的生產」的意圖。
 * Command 是不可變的資料容器，不包含任何業務邏輯。
 */
public class StartProductionCommand implements Command {

    @NotBlank(message = "工單 ID 不可為空")
    private String workOrderId;

    @NotBlank(message = "產品代碼不可為空")
    private String productCode;

    @NotBlank(message = "產線 ID 不可為空")
    private String lineId;

    @NotBlank(message = "產線名稱不可為空")
    private String lineName;

    @NotBlank(message = "操作員 ID 不可為空")
    private String operatorId;

    @NotBlank(message = "操作員姓名不可為空")
    private String operatorName;

    @NotBlank(message = "班次代碼不可為空")
    private String shiftCode;

    public StartProductionCommand() {
    }

    public StartProductionCommand(String workOrderId, String productCode,
                                  String lineId, String lineName,
                                  String operatorId, String operatorName,
                                  String shiftCode) {
        this.workOrderId = workOrderId;
        this.productCode = productCode;
        this.lineId = lineId;
        this.lineName = lineName;
        this.operatorId = operatorId;
        this.operatorName = operatorName;
        this.shiftCode = shiftCode;
    }

    public String getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(String workOrderId) {
        this.workOrderId = workOrderId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getShiftCode() {
        return shiftCode;
    }

    public void setShiftCode(String shiftCode) {
        this.shiftCode = shiftCode;
    }
}
