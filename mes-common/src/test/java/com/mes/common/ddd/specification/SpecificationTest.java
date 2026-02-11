package com.mes.common.ddd.specification;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Specification - 規格模式組合器測試")
class SpecificationTest {

    @Test
    @DisplayName("AND - 兩個規格都滿足時才回傳 true")
    void and_shouldReturnTrueOnlyWhenBothSatisfied() {
        Specification<Integer> greaterThan5 = new GreaterThanSpec(5);
        Specification<Integer> lessThan10 = new LessThanSpec(10);
        Specification<Integer> between = greaterThan5.and(lessThan10);

        assertThat(between.isSatisfiedBy(7)).isTrue();
        assertThat(between.isSatisfiedBy(3)).isFalse();
        assertThat(between.isSatisfiedBy(12)).isFalse();
    }

    @Test
    @DisplayName("OR - 至少一個規格滿足時回傳 true")
    void or_shouldReturnTrueWhenEitherSatisfied() {
        Specification<Integer> spec = new GreaterThanSpec(10).or(new LessThanSpec(3));

        assertThat(spec.isSatisfiedBy(15)).isTrue();
        assertThat(spec.isSatisfiedBy(1)).isTrue();
        assertThat(spec.isSatisfiedBy(5)).isFalse();
    }

    @Test
    @DisplayName("NOT - 反轉規格結果")
    void not_shouldNegateResult() {
        Specification<Integer> notGreaterThan5 = new GreaterThanSpec(5).not();

        assertThat(notGreaterThan5.isSatisfiedBy(3)).isTrue();
        assertThat(notGreaterThan5.isSatisfiedBy(7)).isFalse();
    }

    @Test
    @DisplayName("複合組合 - AND + OR + NOT 可自由組合")
    void complexComposition_shouldWorkCorrectly() {
        // (x > 5 AND x < 20) OR (x < 0)
        Specification<Integer> spec = new GreaterThanSpec(5)
                .and(new LessThanSpec(20))
                .or(new LessThanSpec(0));

        assertThat(spec.isSatisfiedBy(10)).isTrue();   // 5 < 10 < 20
        assertThat(spec.isSatisfiedBy(-5)).isTrue();    // < 0
        assertThat(spec.isSatisfiedBy(3)).isFalse();    // 0 < 3 < 5
        assertThat(spec.isSatisfiedBy(25)).isFalse();   // > 20
    }

    // -- Test specifications --

    static class GreaterThanSpec implements Specification<Integer> {
        private final int threshold;

        GreaterThanSpec(int threshold) {
            this.threshold = threshold;
        }

        @Override
        public boolean isSatisfiedBy(Integer candidate) {
            return candidate > threshold;
        }
    }

    static class LessThanSpec implements Specification<Integer> {
        private final int threshold;

        LessThanSpec(int threshold) {
            this.threshold = threshold;
        }

        @Override
        public boolean isSatisfiedBy(Integer candidate) {
            return candidate < threshold;
        }
    }
}
