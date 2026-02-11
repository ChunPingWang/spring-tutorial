package com.mes.boot.workorder.domain.factory;

import com.mes.boot.workorder.domain.model.DateRange;
import com.mes.boot.workorder.domain.model.Priority;
import com.mes.boot.workorder.domain.model.ProductInfo;
import com.mes.boot.workorder.domain.model.Quantity;
import com.mes.boot.workorder.domain.model.WorkOrder;
import com.mes.boot.workorder.domain.model.WorkOrderId;

import java.util.Objects;

/**
 * [DDD Pattern: Factory]
 * [SOLID: SRP - 只負責工單聚合根的建立與初始驗證]
 *
 * 工單工廠，封裝工單聚合根的複雜建立邏輯。
 * 使用靜態工廠方法模式，確保：
 * <ul>
 *   <li>所有必要參數都經過驗證</li>
 *   <li>產生唯一的 WorkOrderId</li>
 *   <li>工單的初始狀態為 CREATED，並自動註冊 WorkOrderCreatedEvent</li>
 * </ul>
 *
 * 外部應透過此工廠建立工單，而非直接呼叫 WorkOrder 的建構子，
 * 以確保所有前置驗證規則被執行。
 */
public final class WorkOrderFactory {

    private WorkOrderFactory() {
        // 私有建構子，防止實例化
    }

    /**
     * 建立新的工單聚合根。
     * WorkOrderCreatedEvent 會在 WorkOrder 建構時自動註冊。
     *
     * @param productInfo 產品資訊
     * @param quantity    計畫數量
     * @param priority    優先順序
     * @param dateRange   計畫日期範圍
     * @return 已初始化並註冊建立事件的工單
     * @throws NullPointerException     若任何必要參數為 null
     * @throws IllegalArgumentException 若計畫數量為 0
     */
    public static WorkOrder create(ProductInfo productInfo, Quantity quantity,
                                   Priority priority, DateRange dateRange) {
        Objects.requireNonNull(productInfo, "Product info must not be null");
        Objects.requireNonNull(quantity, "Quantity must not be null");
        Objects.requireNonNull(priority, "Priority must not be null");
        Objects.requireNonNull(dateRange, "Date range must not be null");

        if (quantity.getPlanned() <= 0) {
            throw new IllegalArgumentException("Planned quantity must be greater than 0");
        }

        WorkOrderId id = WorkOrderId.generate();
        return new WorkOrder(id, productInfo, quantity, priority, dateRange);
    }
}
