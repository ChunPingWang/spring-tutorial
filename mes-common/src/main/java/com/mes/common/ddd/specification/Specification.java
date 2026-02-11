package com.mes.common.ddd.specification;

/**
 * [DDD Pattern: Specification 規格模式]
 * [SOLID: OCP - 可透過 and/or/not 組合出新規格，無需修改既有規格]
 *
 * Specification 將業務規則封裝為可組合的物件，
 * 用於過濾、驗證或查詢條件的表達。
 *
 * 範例：
 * <pre>
 * Specification<WorkOrder> spec = new OverdueSpec()
 *     .and(new HighPrioritySpec());
 * boolean match = spec.isSatisfiedBy(workOrder);
 * </pre>
 *
 * @param <T> 被檢查物件的型別
 */
public interface Specification<T> {

    /**
     * 檢查候選物件是否滿足此規格。
     */
    boolean isSatisfiedBy(T candidate);

    /**
     * 與另一個規格進行 AND 組合。
     */
    default Specification<T> and(Specification<T> other) {
        return new AndSpecification<>(this, other);
    }

    /**
     * 與另一個規格進行 OR 組合。
     */
    default Specification<T> or(Specification<T> other) {
        return new OrSpecification<>(this, other);
    }

    /**
     * 取此規格的 NOT（反向）。
     */
    default Specification<T> not() {
        return new NotSpecification<>(this);
    }
}
