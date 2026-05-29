package org.sopt.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
        @Schema(description = "이메일", example = "jsshin@test.com")
        String email,

        @Schema(description = "비밀번호", example = "pw1234")
        String password
) {
}