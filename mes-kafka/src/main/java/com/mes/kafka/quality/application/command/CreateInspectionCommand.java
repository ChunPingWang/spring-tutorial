package com.mes.kafka.quality.application.command;

import com.mes.common.cqrs.Command;

/**
 * [CQRS Pattern: Command - 建立檢驗工單命令]
 * [SOLID: SRP - 只攜帶建立檢驗工單所需的資料]
 *
 * 建立新的品質檢驗工單命令。
 * 命令為不可變物件，建構後不可修改。
 */
public class CreateInspectionCommand implements Command {

    private final String workOrderId;
    private final String productCode;
    private final String type;

    public CreateInspectionCommand(String workOrderId, String productCode, String type) {
        this.workOrderId = workOrderId;
        this.productCode = productCode;
        this.type = type;
    }

    public String getWorkOrderId() {
        return workOrderId;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getType() {
        return type;
    }
}
