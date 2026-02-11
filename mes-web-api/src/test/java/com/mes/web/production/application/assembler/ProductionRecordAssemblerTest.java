package com.mes.web.production.application.assembler;

import com.mes.web.production.application.query.dto.ProductionRecordView;
import com.mes.web.production.domain.model.OperatorInfo;
import com.mes.web.production.domain.model.OutputQuantity;
import com.mes.web.production.domain.model.ProductionLine;
import com.mes.web.production.domain.model.ProductionLineId;
import com.mes.web.production.domain.model.ProductionRecord;
import com.mes.web.production.domain.model.ProductionRecordId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * [DDD Pattern: Assembler 單元測試]
 *
 * 驗證 ProductionRecordAssembler 的映射正確性：
 * 1. 單筆記錄轉換
 * 2. 列表轉換
 * 3. null 處理
 */
@DisplayName("ProductionRecordAssembler 測試")
class ProductionRecordAssemblerTest {

    @Test
    @DisplayName("應正確將聚合根轉換為檢視 DTO")
    void shouldConvertRecordToView() {
        // Arrange
        ProductionRecord record = createTestRecord("PR-001", "WO-001", "PROD-A");
        record.recordOutput(new OutputQuantity(90, 8, 2));

        // Act
        ProductionRecordView view = ProductionRecordAssembler.toView(record);

        // Assert
        assertThat(view.getId()).isEqualTo("PR-001");
        assertThat(view.getWorkOrderId()).isEqualTo("WO-001");
        assertThat(view.getProductCode()).isEqualTo("PROD-A");
        assertThat(view.getStatus()).isEqualTo("PENDING");
        assertThat(view.getStatusDescription()).isEqualTo("待開始");
        assertThat(view.getGoodQuantity()).isEqualTo(90);
        assertThat(view.getDefectiveQuantity()).isEqualTo(8);
        assertThat(view.getReworkQuantity()).isEqualTo(2);
        assertThat(view.getTotalQuantity()).isEqualTo(100);
        assertThat(view.getOperatorId()).isEqualTo("OP-001");
        assertThat(view.getOperatorName()).isEqualTo("王小明");
        assertThat(view.getShiftCode()).isEqualTo("DAY");
        assertThat(view.getProductionLine().getLineId()).isEqualTo("LINE-A");
        assertThat(view.getProductionLine().getLineName()).isEqualTo("A 產線");
        assertThat(view.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("null 輸入應回傳 null")
    void shouldReturnNullForNullInput() {
        assertThat(ProductionRecordAssembler.toView(null)).isNull();
    }

    @Test
    @DisplayName("應正確轉換列表")
    void shouldConvertList() {
        // Arrange
        ProductionRecord record1 = createTestRecord("PR-001", "WO-001", "PROD-A");
        ProductionRecord record2 = createTestRecord("PR-002", "WO-002", "PROD-B");
        List<ProductionRecord> records = Arrays.asList(record1, record2);

        // Act
        List<ProductionRecordView> views = ProductionRecordAssembler.toViewList(records);

        // Assert
        assertThat(views).hasSize(2);
        assertThat(views.get(0).getId()).isEqualTo("PR-001");
        assertThat(views.get(1).getId()).isEqualTo("PR-002");
    }

    @Test
    @DisplayName("空列表應回傳空列表")
    void shouldReturnEmptyListForEmptyInput() {
        List<ProductionRecordView> views = ProductionRecordAssembler.toViewList(
                Collections.<ProductionRecord>emptyList());
        assertThat(views).isEmpty();
    }

    @Test
    @DisplayName("null 列表應回傳空列表")
    void shouldReturnEmptyListForNullInput() {
        List<ProductionRecordView> views = ProductionRecordAssembler.toViewList(null);
        assertThat(views).isEmpty();
    }

    private ProductionRecord createTestRecord(String id, String workOrderId, String productCode) {
        ProductionRecordId recordId = ProductionRecordId.of(id);
        ProductionLineId lineId = ProductionLineId.of("LINE-A");
        ProductionLine line = new ProductionLine(lineId, "A 產線");
        OperatorInfo operator = new OperatorInfo("OP-001", "王小明", "DAY");

        return new ProductionRecord(recordId, line, workOrderId, productCode, operator);
    }
}
