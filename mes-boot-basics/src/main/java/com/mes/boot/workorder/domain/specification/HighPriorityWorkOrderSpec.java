package com.mes.boot.workorder.domain.specification;

import com.mes.boot.workorder.domain.model.WorkOrder;
import com.mes.common.ddd.specification.Specification;

/**
 * [DDD Pattern: Specification]
 * [SOLID: SRP - 只負責判斷工單是否為高優先順序]
 * [SOLID: OCP - 可透過 and/or/not 與其他 Specification 組合]
 *
 * 高優先工單規格，檢查工單的優先順序是否為 HIGH 或 URGENT。
 *
 * 使用方式：
 * <pre>
 *   Specification&lt;WorkOrder&gt; spec = new HighPriorityWorkOrderSpec();
 *   boolean isHigh = spec.isSatisfiedBy(workOrder);
 *
 *   // 組合使用：找出逾期且高優先的工單
 *   Specification&lt;WorkOrder&gt; criticalSpec = spec.and(new OverdueWorkOrderSpec());
 * </pre>
 */
public class HighPriorityWorkOrderSpec implements Specification<WorkOrder> {

    /**
     * 判斷工單是否為高優先順序（HIGH 或 URGENT）。
     *
     * @param candidate 要檢查的工單
     * @return 若為高優先回傳 true
     */
    @Override
    public boolean isSatisfiedBy(WorkOrder candidate) {
        return candidate.getPriority().isHighPriority();
    }
}
