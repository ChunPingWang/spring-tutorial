package com.mes.common.exception;

/**
 * 領域層基礎例外。
 * 當領域規則被違反時拋出（如不合法的狀態轉換）。
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
