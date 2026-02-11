package com.mes.boot.workorder.application.assembler;

import com.mes.boot.workorder.application.dto.CreateWorkOrderRequest;
import com.mes.boot.workorder.application.dto.WorkOrderResponse;
import com.mes.boot.workorder.domain.factory.WorkOrderFactory;
import com.mes.boot.workorder.domain.model.DateRange;
import com.mes.boot.workorder.domain.model.Priority;
import com.mes.boot.workorder.domain.model.ProductInfo;
import com.mes.boot.workorder.domain.model.Quantity;
import com.mes.boot.workorder.domain.model.WorkOrder;

/**
 * [DDD Pattern: Assembler / Mapper]
 * [SOLID: SRP - 只負責領域模型與 DTO 之間的轉換]
 *
 * 工單組裝器，負責將領域物件（WorkOrder）轉換為 DTO（WorkOrderResponse），
 * 以及將 DTO（CreateWorkOrderRequest）轉換為領域物件。
 *
 * 在六角架構中，Assembler 位於應用層，
 * 是連接領域層與外部世界的橋樑。
 *
 * 使用靜態方法避免不必要的實例化，
 * 因為 Assembler 不持有任何狀態。
 */
public final class WorkOrderAssembler {

    private WorkOrderAssembler() {
        // 私有建構子，防止實例化
    }

    /**
     * 將領域物件 WorkOrder 轉換為 DTO WorkOrderResponse。
     *
     * @param workOrder 領域物件
     * @return 回應 DTO
     */
    public static WorkOrderResponse toResponse(WorkOrder workOrder) {
        WorkOrderResponse response = new WorkOrderResponse();
        response.setId(workOrder.getId().getValue());
        response.setStatus(workOrder.getStatus().name());
        response.setProductCode(workOrder.getProductInfo().getProductCode());
        response.setProductName(workOrder.getProductInfo().getProductName());
        response.setPlanned(workOrder.getQuantity().getPlanned());
        response.setCompleted(workOrder.getQuantity().getCompleted());
        response.setDefective(workOrder.getQuantity().getDefective());
        response.setPriority(workOrder.getPriority().name());
        response.setPlannedStart(workOrder.getDateRange().getPlannedStart());
        response.setPlannedEnd(workOrder.getDateRange().getPlannedEnd());
        response.setCreatedAt(workOrder.getCreatedAt());
        return response;
    }

    /**
     * 將 DTO CreateWorkOrderRequest 轉換為領域物件 WorkOrder。
     * 透過 {@link WorkOrderFactory} 建立，確保領域規則被正確執行。
     *
     * @param request 建立工單請求 DTO
     * @return 已建立的工單領域物件
     */
    public static WorkOrder toDomain(CreateWorkOrderRequest request) {
        ProductInfo productInfo = new ProductInfo(
                request.getProductCode(),
                request.getProductName(),
                request.getSpecification());

        Quantity quantity = Quantity.ofPlanned(request.getPlannedQuantity());

        Priority priority = Priority.valueOf(request.getPriority().toUpperCase());

        DateRange dateRange = new DateRange(
                request.getPlannedStart(),
                request.getPlannedEnd());

        return WorkOrderFactory.create(productInfo, quantity, priority, dateRange);
    }
}
