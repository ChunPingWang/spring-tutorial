package com.mes.kafka.quality.adapter.in.web;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * [Hexagonal Architecture: 統一 API 回應格式]
 * [SOLID: SRP - 只負責封裝 HTTP API 的回應結構]
 *
 * 統一的 REST API 回應包裝物件。
 * 所有 API 端點都使用此格式回傳，確保前端介面一致性。
 *
 * @param <T> 回應資料的型別
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;

    private ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * 建立成功回應（含資料）。
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, null, data);
    }

    /**
     * 建立成功回應（含訊息與資料）。
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * 建立錯誤回應。
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
