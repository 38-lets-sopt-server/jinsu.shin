package org.sopt.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    POST_001(HttpStatus.NOT_FOUND,   "POST_001", "게시글을 찾을 수 없습니다."),
    POST_002(HttpStatus.BAD_REQUEST, "POST_002", "제목은 필수입니다."),
    POST_003(HttpStatus.BAD_REQUEST, "POST_003", "제목은 50자 이하여야 합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getStatus() { return status; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
}
