package org.sopt.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserCreateRequest(
        @Schema(description = "닉네임", example = "진수")
        String nickname,

        @Schema(description = "이메일", example = "jsshin@test.com")
        String email,

        @Schema(description = "비밀번호", example = "pw1234")
        String password
) {
}
