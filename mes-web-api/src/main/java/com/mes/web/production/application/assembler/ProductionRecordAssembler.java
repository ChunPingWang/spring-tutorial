package com.mes.web.production.application.assembler;

import com.mes.web.production.application.query.dto.ProductionLineView;
import com.mes.web.production.application.query.dto.ProductionRecordView;
import com.mes.web.production.domain.model.ProductionRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * [DDD Pattern: Assembler - 領域物件與 DTO 的轉換]
 * [SOLID: SRP - 只負責 ProductionRecord 與 ProductionRecordView 之間的映射]
 *
 * Assembler 負責在不同層的物件之間進行轉換。
 * 在 CQRS 中，Assembler 將 Domain Model (寫入模型) 轉換為 Read Model (讀取模型)。
 *
 * 使用靜態方法以保持無狀態，避免不必要的物件建立。
 */
public final class ProductionRecordAssembler {

    private ProductionRecordAssembler() {
        // 工具類別，禁止實例化
    }

    /**
     * 將 ProductionRecord 聚合根轉換為 ProductionRecordView DTO。
     *
     * @param record 生產紀錄聚合根
     * @return 生產紀錄檢視 DTO
     */
    public static ProductionRecordView toView(ProductionRecord record) {
        if (record == null) {
            return null;
        }

        ProductionRecordView view = new ProductionRecordView();
        view.setId(record.getId().getValue());
        view.setWorkOrderId(record.getWorkOrderId());
        view.setProductCode(record.getProductCode());
        view.setStatus(record.getStatus().name());
        view.setStatusDescription(record.getStatus().getDescription());

        // 產線資訊
        ProductionLineView lineView = new ProductionLineView(
                record.getProductionLine().getLineId().getValue(),
                record.getProductionLine().getLineName());
        view.setProductionLine(lineView);

        // 產出資訊
        view.setGoodQuantity(record.getOutput().getGood());
        view.setDefectiveQuantity(record.getOutput().getDefective());
        view.setReworkQuantity(record.getOutput().getRework());
        view.setTotalQuantity(record.getOutput().getTotal());
        view.setYieldRate(record.getOutput().getYieldRate());

        // 操作員資訊
        view.setOperatorId(record.getOperator().getOperatorId());
        view.setOperatorName(record.getOperator().getOperatorName());
        view.setShiftCode(record.getOperator().getShiftCode());

        // 時間戳記
        view.setCreatedAt(record.getCreatedAt());
        view.setUpdatedAt(record.getUpdatedAt());

        return view;
    }

    /**
     * 將多筆 ProductionRecord 轉換為 ProductionRecordView 列表。
     *
     * @param records 生產紀錄列表
     * @return 生產紀錄檢視列表
     */
    public static List<ProductionRecordView> toViewList(List<ProductionRecord> records) {
        List<ProductionRecordView> views = new ArrayList<ProductionRecordView>();
        if (records != null) {
            for (ProductionRecord record : records) {
                views.add(toView(record));
            }
        }
        return views;
    }
}
