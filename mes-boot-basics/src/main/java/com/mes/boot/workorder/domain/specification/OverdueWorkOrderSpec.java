package com.mes.boot.workorder.domain.specification;

import com.mes.boot.workorder.domain.model.WorkOrder;
import com.mes.boot.workorder.domain.model.WorkOrderStatus;
import com.mes.common.ddd.specification.Specification;

import java.time.LocalDate;

/**
 * [DDD Pattern: Specification]
 * [SOLID: SRP - 只負責判斷工單是否已逾期]
 * [SOLID: OCP - 可透過 and/or/not 與其他 Specification 組合]
 *
 * 逾期工單規格，檢查工單的計畫結束日期是否已經過去，
 * 且工單尚未完成或取消。
 *
 * 使用方式：
 * <pre>
 *   Specification&lt;WorkOrder&gt; spec = new OverdueWorkOrderSpec();
 *   boolean isOverdue = spec.isSatisfiedBy(workOrder);
 * </pre>
 */
public class OverdueWorkOrderSpec implements Specification<WorkOrder> {

    /**
     * 判斷工單是否逾期。
     * 條件：計畫結束日期早於今日，且工單狀態不是 COMPLETED 或 CANCELLED。
     *
     * @param candidate 要檢查的工單
     * @return 若逾期回傳 true
     */
    @Override
    public boolean isSatisfiedBy(WorkOrder candidate) {
        boolean isPastDue = candidate.getDateRange().getPlannedEnd().isBefore(LocalDate.now());
        boolean isNotFinished = candidate.getStatus() != WorkOrderStatus.COMPLETED
                && candidate.getStatus() != WorkOrderStatus.CANCELLED;
        return isPastDue && isNotFinished;
    }
}
