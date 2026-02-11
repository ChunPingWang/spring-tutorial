package com.mes.web.production.application.command;

import com.mes.common.cqrs.Command;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * [CQRS Pattern: Command - 記錄產出]
 * [SOLID: SRP - 只封裝記錄產出所需的資料]
 *
 * 代表「記錄一筆產出數量」的意圖。
 * 包含良品數、不良品數、重工品數。
 */
public class RecordOutputCommand implements Command {

    @NotBlank(message = "生產紀錄 ID 不可為空")
    private String productionRecordId;

    @NotNull(message = "良品數量不可為空")
    @Min(value = 0, message = "良品數量不可為負數")
    private Integer good;

    @NotNull(message = "不良品數量不可為空")
    @Min(value = 0, message = "不良品數量不可為負數")
    private Integer defective;

    @NotNull(message = "重工品數量不可為空")
    @Min(value = 0, message = "重工品數量不可為負數")
    private Integer rework;

    public RecordOutputCommand() {
    }

    public RecordOutputCommand(String productionRecordId, int good, int defective, int rework) {
        this.productionRecordId = productionRecordId;
        this.good = good;
        this.defective = defective;
        this.rework = rework;
    }

    public String getProductionRecordId() {
        return productionRecordId;
    }

    public void setProductionRecordId(String productionRecordId) {
        this.productionRecordId = productionRecordId;
    }

    public Integer getGood() {
        return good;
    }

    public void setGood(Integer good) {
        this.good = good;
    }

    public Integer getDefective() {
        return defective;
    }

    public void setDefective(Integer defective) {
        this.defective = defective;
    }

    public Integer getRework() {
        return rework;
    }

    public void setRework(Integer rework) {
        this.rework = rework;
    }
}
