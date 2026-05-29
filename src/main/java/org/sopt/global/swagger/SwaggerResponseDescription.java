package org.sopt.global.swagger;

import org.sopt.global.exception.ErrorCode;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.sopt.global.exception.ErrorCode.ALREADY_LIKED;
import static org.sopt.global.exception.ErrorCode.EMAIL_ALREADY_EXISTS;
import static org.sopt.global.exception.ErrorCode.FORBIDDEN;
import static org.sopt.global.exception.ErrorCode.INTERNAL_SERVER_ERROR;
import static org.sopt.global.exception.ErrorCode.INVALID_CREDENTIALS;
import static org.sopt.global.exception.ErrorCode.INVALID_INPUT;
import static org.sopt.global.exception.ErrorCode.INVALID_TOKEN;
import static org.sopt.global.exception.ErrorCode.LIKE_NOT_FOUND;
import static org.sopt.global.exception.ErrorCode.OAUTH_AUTHENTICATION_FAILED;
import static org.sopt.global.exception.ErrorCode.OAUTH_EMAIL_ALREADY_REGISTERED;
import static org.sopt.global.exception.ErrorCode.OAUTH_SERVER_ERROR;
import static org.sopt.global.exception.ErrorCode.POST_NOT_FOUND;
import static org.sopt.global.exception.ErrorCode.POST_TITLE_REQUIRED;
import static org.sopt.global.exception.ErrorCode.POST_TITLE_TOO_LONG;
import static org.sopt.global.exception.ErrorCode.UNAUTHORIZED;
import static org.sopt.global.exception.ErrorCode.USER_NOT_FOUND;

public enum SwaggerResponseDescription {

    LOGIN(Set.of(INVALID_CREDENTIALS)),
    REISSUE(Set.of(INVALID_TOKEN)),
    LOGOUT(Set.of(UNAUTHORIZED, INVALID_TOKEN)),
    GET_MY_INFO(Set.of(UNAUTHORIZED, USER_NOT_FOUND)),
    KAKAO_LOGIN(Set.of(OAUTH_AUTHENTICATION_FAILED, OAUTH_EMAIL_ALREADY_REGISTERED, OAUTH_SERVER_ERROR)),
    SIGNUP(Set.of(EMAIL_ALREADY_EXISTS)),
    CREATE_POST(Set.of(POST_TITLE_REQUIRED, POST_TITLE_TOO_LONG, UNAUTHORIZED, USER_NOT_FOUND)),
    GET_POST(Set.of(POST_NOT_FOUND)),
    UPDATE_POST(Set.of(POST_TITLE_REQUIRED, POST_TITLE_TOO_LONG, UNAUTHORIZED, FORBIDDEN, POST_NOT_FOUND)),
    DELETE_POST(Set.of(UNAUTHORIZED, FORBIDDEN, POST_NOT_FOUND)),
    ADD_LIKE(Set.of(UNAUTHORIZED, POST_NOT_FOUND, USER_NOT_FOUND, ALREADY_LIKED)),
    CANCEL_LIKE(Set.of(UNAUTHORIZED, POST_NOT_FOUND, USER_NOT_FOUND, LIKE_NOT_FOUND));

    private static final Set<ErrorCode> COMMON = Set.of(INVALID_INPUT, INTERNAL_SERVER_ERROR);

    private final Set<ErrorCode> errorCodes;

    SwaggerResponseDescription(Set<ErrorCode> errorCodes) {
        this.errorCodes = errorCodes;
    }

    public static Set<ErrorCode> common() {
        return COMMON;
    }

    public Set<ErrorCode> getErrorCodes() {
        Set<ErrorCode> merged = new LinkedHashSet<>(errorCodes);
        merged.addAll(COMMON);
        return merged;
    }
}