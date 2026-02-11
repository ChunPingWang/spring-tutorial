package com.mes.common.ddd.specification;

/**
 * NOT 規格：反轉原規格的結果。
 */
public class NotSpecification<T> implements Specification<T> {

    private final Specification<T> spec;

    public NotSpecification(Specification<T> spec) {
        this.spec = spec;
    }

    @Override
    public boolean isSatisfiedBy(T candidate) {
        return !spec.isSatisfiedBy(candidate);
    }
}
