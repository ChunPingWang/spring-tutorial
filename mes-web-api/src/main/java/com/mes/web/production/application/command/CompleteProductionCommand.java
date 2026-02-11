package com.mes.web.production.application.command;

import com.mes.common.cqrs.Command;

import javax.validation.constraints.NotBlank;

/**
 * [CQRS Pattern: Command - 完成生產]
 * [SOLID: SRP - 只封裝完成生產所需的資料]
 *
 * 代表「完成一筆生產」的意圖。
 * 只需要生產紀錄 ID 即可。
 */
public class CompleteProductionCommand implements Command {

    @NotBlank(message = "生產紀錄 ID 不可為空")
    private String productionRecordId;

    public CompleteProductionCommand() {
    }

    public CompleteProductionCommand(String productionRecordId) {
        this.productionRecordId = productionRecordId;
    }

    public String getProductionRecordId() {
        return productionRecordId;
    }

    public void setProductionRecordId(String productionRecordId) {
        this.productionRecordId = productionRecordId;
    }
}
