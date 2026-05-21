package org.sopt.global.exception;

public enum SuccessCode {

    // ===== COMMON =====
    OK(200, "요청이 성공했습니다."),
    CREATED(201, "리소스가 생성되었습니다."),
    NO_CONTENT(204, "처리되었습니다.");

    private final int status;
    private final String message;

    SuccessCode(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}