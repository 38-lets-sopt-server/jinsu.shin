package org.sopt.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record KakaoLoginRequest(
        @Schema(description = "카카오로부터 받은 authorization code", example = "abcdef123456...")
        String code
) {
}