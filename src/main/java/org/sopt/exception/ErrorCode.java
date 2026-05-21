package org.sopt.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    POST_001(HttpStatus.NOT_FOUND,   "POST_001", "게시글을 찾을 수 없습니다."),
    POST_002(HttpStatus.BAD_REQUEST, "POST_002", "제목은 필수입니다."),
    POST_003(HttpStatus.BAD_REQUEST, "POST_003", "제목은 50자 이하여야 합니다."),
    USER_001(HttpStatus.NOT_FOUND,    "USER_001", "사용자를 찾을 수 없습니다."),
    USER_002(HttpStatus.CONFLICT,     "USER_002", "이미 가입된 이메일입니다."),
    AUTH_001(HttpStatus.UNAUTHORIZED, "AUTH_001", "이메일 또는 비밀번호가 올바르지 않습니다."),
    AUTH_002(HttpStatus.UNAUTHORIZED, "AUTH_002", "유효하지 않은 토큰입니다."),
    AUTH_003(HttpStatus.UNAUTHORIZED, "AUTH_003", "인증되지 않은 요청입니다."),
    LIKE_001(HttpStatus.CONFLICT,     "LIKE_001", "이미 좋아요를 눌렀습니다."),
    LIKE_002(HttpStatus.NOT_FOUND,    "LIKE_002", "좋아요를 찾을 수 없습니다.");

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
