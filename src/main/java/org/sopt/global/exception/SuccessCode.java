package org.sopt.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    // ===== COMMON =====
    OK(200, "요청이 성공했습니다."),
    CREATED(201, "리소스가 생성되었습니다.");

    private final int status;
    private final String message;
}
