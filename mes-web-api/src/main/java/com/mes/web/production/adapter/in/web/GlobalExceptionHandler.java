package com.mes.web.production.adapter.in.web;

import com.mes.common.exception.BusinessRuleViolationException;
import com.mes.common.exception.DomainException;
import com.mes.common.exception.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * [Hexagonal Architecture: Adapter - 全域例外處理]
 * [SOLID: SRP - 只負責將領域例外轉換為 HTTP 回應]
 * [SOLID: OCP - 新增例外類型只需新增 @ExceptionHandler 方法]
 *
 * 全域例外處理器，將領域層的例外轉換為適當的 HTTP 回應：
 * - EntityNotFoundException   -> 404 Not Found
 * - BusinessRuleViolationException -> 409 Conflict
 * - DomainException          -> 422 Unprocessable Entity
 * - MethodArgumentNotValidException -> 400 Bad Request
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 處理實體未找到例外 -> 404。
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleEntityNotFound(EntityNotFoundException ex) {
        log.warn("實體未找到: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<Void>error(404, ex.getMessage()));
    }

    /**
     * 處理業務規則違反例外 -> 409。
     */
    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessRuleViolation(
            BusinessRuleViolationException ex) {
        log.warn("業務規則違反: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.<Void>error(409, ex.getMessage()));
    }

    /**
     * 處理領域例外 -> 422。
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiResponse<Void>> handleDomainException(DomainException ex) {
        log.warn("領域例外: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResponse.<Void>error(422, ex.getMessage()));
    }

    /**
     * 處理 Bean Validation 驗證失敗 -> 400。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<String>>> handleValidationException(
            MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<String>();
        ex.getBindingResult().getFieldErrors().forEach(
                error -> errors.add(error.getField() + ": " + error.getDefaultMessage()));
        log.warn("請求驗證失敗: {}", errors);
        ApiResponse<List<String>> response = new ApiResponse<List<String>>(400, "請求參數驗證失敗", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }
}
