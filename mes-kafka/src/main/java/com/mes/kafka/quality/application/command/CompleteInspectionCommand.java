package com.mes.kafka.quality.application.command;

import com.mes.common.cqrs.Command;

/**
 * [CQRS Pattern: Command - 完成檢驗命令]
 * [SOLID: SRP - 只攜帶完成檢驗所需的資料]
 *
 * 完成品質檢驗工單命令。
 * 執行後會根據檢驗結果判定最終狀態（PASSED / FAILED）。
 */
public class CompleteInspectionCommand implements Command {

    private final String inspectionOrderId;

    public CompleteInspectionCommand(String inspectionOrderId) {
        this.inspectionOrderId = inspectionOrderId;
    }

    public String getInspectionOrderId() {
        return inspectionOrderId;
    }
}
