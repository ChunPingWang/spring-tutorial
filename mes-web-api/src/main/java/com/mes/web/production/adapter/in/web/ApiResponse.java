package com.mes.web.production.adapter.in.web;

import java.time.LocalDateTime;

/**
 * [Hexagonal Architecture: Adapter - 統一 API 回應封裝]
 * [SOLID: SRP - 只負責封裝 API 回應的統一格式]
 *
 * 通用的 API 回應封裝，提供一致的回應結構：
 * - code: HTTP 狀態碼或自定義代碼
 * - message: 回應訊息
 * - data: 回應資料（泛型）
 * - timestamp: 回應時間戳記
 *
 * @param <T> 回應資料的型別
 */
public class ApiResponse<T> {

    private int code;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 建立成功回應（200）。
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<T>(200, "success", data);
    }

    /**
     * 建立成功回應帶自定義訊息。
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<T>(200, message, data);
    }

    /**
     * 建立已建立回應（201）。
     */
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<T>(201, "created", data);
    }

    /**
     * 建立錯誤回應。
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<T>(code, message, null);
    }

    // ========== Getters and Setters ==========

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
