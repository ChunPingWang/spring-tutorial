package com.mes.kafka.quality.application.assembler;

import com.mes.kafka.quality.application.query.dto.InspectionOrderView;
import com.mes.kafka.quality.domain.model.InspectionOrder;
import org.springframework.stereotype.Component;

/**
 * [DDD Pattern: Assembler - 領域模型與 DTO 的轉換器]
 * [SOLID: SRP - 只負責 InspectionOrder 領域模型與視圖 DTO 之間的轉換]
 * [SOLID: OCP - 新增視圖類型只需增加新的轉換方法]
 *
 * Assembler 負責將領域模型轉換為應用層的 DTO（View），
 * 防止領域模型的內部結構洩漏到外部。
 */
@Component
public class InspectionAssembler {

    /**
     * 將 InspectionOrder 聚合根轉換為 InspectionOrderView。
     *
     * @param order 檢驗工單聚合根
     * @return 檢驗工單唯讀視圖
     */
    public InspectionOrderView toView(InspectionOrder order) {
        return new InspectionOrderView(
                order.getId().getValue(),
                order.getWorkOrderId(),
                order.getProductCode(),
                order.getType().name(),
                order.getStatus().name(),
                order.getResults().size(),
                order.getDefectRate()
        );
    }
}
