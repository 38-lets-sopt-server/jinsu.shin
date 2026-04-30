package org.sopt.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

public record ApiResponse<T>(
        boolean success,
        @JsonInclude(JsonInclude.Include.NON_NULL) String code,
        String message,
        T data
) {
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, null, message, data);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, null, "요청이 성공했습니다.", data);
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message, null);
    }
}
