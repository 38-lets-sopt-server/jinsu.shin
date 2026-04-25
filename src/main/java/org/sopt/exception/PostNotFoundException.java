package org.sopt.exception;

public class PostNotFoundException extends RuntimeException {
    private final ErrorCode errorCode = ErrorCode.POST_001;

    public PostNotFoundException(Long id) {
        super(ErrorCode.POST_001.getCode() + ": " + ErrorCode.POST_001.getMessage() + " id: " + id);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
