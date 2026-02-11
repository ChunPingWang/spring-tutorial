package com.mes.common.exception;

/**
 * 當業務規則被違反時拋出。
 * 與 DomainException 區分：BusinessRuleViolation 更側重於業務邏輯層面的違規。
 */
public class BusinessRuleViolationException extends DomainException {

    public BusinessRuleViolationException(String message) {
        super(message);
    }
}
