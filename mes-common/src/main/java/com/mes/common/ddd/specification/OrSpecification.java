package com.mes.common.ddd.specification;

/**
 * OR 組合規格：至少一個規格必須滿足。
 */
public class OrSpecification<T> implements Specification<T> {

    private final Specification<T> left;
    private final Specification<T> right;

    public OrSpecification(Specification<T> left, Specification<T> right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean isSatisfiedBy(T candidate) {
        return left.isSatisfiedBy(candidate) || right.isSatisfiedBy(candidate);
    }
}
