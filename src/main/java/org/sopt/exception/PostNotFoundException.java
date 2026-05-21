package org.sopt.exception;

import org.sopt.global.exception.ErrorCode;

public class PostNotFoundException extends RuntimeException {
    private final ErrorCode errorCode = ErrorCode.POS_404_001;

    public PostNotFoundException(Long id) {
        super(ErrorCode.POS_404_001.getCode() + ": " + ErrorCode.POS_404_001.getMessage() + " id: " + id);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
