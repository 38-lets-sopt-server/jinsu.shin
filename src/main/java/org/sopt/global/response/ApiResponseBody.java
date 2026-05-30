package org.sopt.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.sopt.global.exception.ErrorCode;
import org.sopt.global.exception.SuccessCode;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponseBody<T, M>(
        boolean success,
        int status,
        String message,
        T data,
        String code,
        M meta
) {

    public static ApiResponseBody<Void, Void> ok() {
        return new ApiResponseBody<>(true, SuccessCode.OK.getStatus(), SuccessCode.OK.getMessage(), null, null, null);
    }

    public static <T> ApiResponseBody<T, Void> ok(T data) {
        return new ApiResponseBody<>(true, SuccessCode.OK.getStatus(), SuccessCode.OK.getMessage(), data, null, null);
    }

    public static <T> ApiResponseBody<T, Void> created(T data) {
        return new ApiResponseBody<>(true, SuccessCode.CREATED.getStatus(), SuccessCode.CREATED.getMessage(), data, null, null);
    }

    public static ApiResponseBody<Void, ErrorMeta> onFailure(ErrorCode errorCode, ErrorMeta errorMeta) {
        return new ApiResponseBody<>(
                false,
                errorCode.getStatus(),
                errorCode.getMessage(),
                null,
                errorCode.getCode(),
                errorMeta
        );
    }

    public static ApiResponseBody<Void, ErrorMeta> onFailure(ErrorCode errorCode, String message, ErrorMeta errorMeta) {
        return new ApiResponseBody<>(
                false,
                errorCode.getStatus(),
                message,
                null,
                errorCode.getCode(),
                errorMeta
        );
    }
}
