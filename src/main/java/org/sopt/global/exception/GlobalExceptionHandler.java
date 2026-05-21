package org.sopt.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.sopt.global.exception.BusinessException;
import org.sopt.global.exception.ErrorCode;
import org.sopt.global.response.ApiResponseBody;
import org.sopt.global.response.ErrorMeta;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponseBody<Void, ErrorMeta>> handleBusiness(
            BusinessException e, HttpServletRequest request
    ) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorMeta meta = new ErrorMeta(request.getRequestURI(), System.currentTimeMillis());
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponseBody.onFailure(errorCode, meta));
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponseBody<Void, ErrorMeta>> handleOptimisticLocking(
            ObjectOptimisticLockingFailureException e, HttpServletRequest request
    ) {
        ErrorMeta meta = new ErrorMeta(request.getRequestURI(), System.currentTimeMillis());
        return ResponseEntity.status(ErrorCode.COM_409_001.getStatus())
                .body(ApiResponseBody.onFailure(ErrorCode.COM_409_001, meta));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponseBody<Void, ErrorMeta>> handleTypeMismatch(
            MethodArgumentTypeMismatchException e, HttpServletRequest request
    ) {
        String message = e.getName() + "에 잘못된 값이 입력되었습니다: " + e.getValue();
        ErrorMeta meta = new ErrorMeta(request.getRequestURI(), System.currentTimeMillis());
        return ResponseEntity.status(ErrorCode.COM_400_001.getStatus())
                .body(ApiResponseBody.onFailure(ErrorCode.COM_400_001, message, meta));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseBody<Void, ErrorMeta>> handleException(
            Exception e, HttpServletRequest request
    ) {
        ErrorMeta meta = new ErrorMeta(request.getRequestURI(), System.currentTimeMillis());
        return ResponseEntity.status(ErrorCode.COM_500_001.getStatus())
                .body(ApiResponseBody.onFailure(ErrorCode.COM_500_001, meta));
    }
}
