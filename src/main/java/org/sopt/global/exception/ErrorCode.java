package org.sopt.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ===== COMMON =====
    INVALID_INPUT(400, "COM_400_001", "잘못된 요청 값입니다."),
    CONCURRENT_REQUEST(409, "COM_409_001", "동시 요청으로 인해 처리에 실패했습니다. 다시 시도해주세요."),
    INTERNAL_SERVER_ERROR(500, "COM_500_001", "서버 내부 오류가 발생했습니다."),

    // ===== POST =====
    POST_TITLE_REQUIRED(400, "POS_400_001", "제목은 필수입니다."),
    POST_TITLE_TOO_LONG(400, "POS_400_002", "제목은 50자 이하여야 합니다."),
    POST_NOT_FOUND(404, "POS_404_001", "게시글을 찾을 수 없습니다."),

    // ===== USER =====
    USER_NOT_FOUND(404, "USR_404_001", "사용자를 찾을 수 없습니다."),
    EMAIL_ALREADY_EXISTS(409, "USR_409_001", "이미 가입된 이메일입니다."),

    // ===== AUTH =====
    INVALID_CREDENTIALS(401, "ATH_401_001", "이메일 또는 비밀번호가 올바르지 않습니다."),
    INVALID_TOKEN(401, "ATH_401_002", "유효하지 않은 토큰입니다."),
    UNAUTHORIZED(401, "ATH_401_003", "인증되지 않은 요청입니다."),
    BLACKLISTED_TOKEN(401, "ATH_401_004", "만료되었거나 로그아웃된 토큰입니다."),
    OAUTH_AUTHENTICATION_FAILED(401, "ATH_401_005", "OAuth 인증에 실패했습니다."),
    ACCESS_TOKEN_EXPIRED(401, "ATH_401_006", "Access Token이 만료되었습니다."),
    REFRESH_TOKEN_EXPIRED(401, "ATH_401_007", "Refresh Token이 만료되었습니다."),
    FORBIDDEN(403, "ATH_403_001", "권한이 없습니다."),
    OAUTH_EMAIL_ALREADY_REGISTERED(409, "ATH_409_002", "이미 가입된 이메일입니다. 기존 계정으로 로그인해주세요."),
    OAUTH_SERVER_ERROR(500, "ATH_500_001", "외부 OAuth 서버 통신에 실패했습니다."),

    // ===== LIKE =====
    LIKE_NOT_FOUND(404, "LIK_404_001", "좋아요를 찾을 수 없습니다."),
    ALREADY_LIKED(409, "LIK_409_001", "이미 좋아요를 눌렀습니다.");

    private final int status;
    private final String code;
    private final String message;
}
