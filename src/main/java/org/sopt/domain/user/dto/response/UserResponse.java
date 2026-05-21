package org.sopt.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.domain.user.entity.User;

public record UserResponse(
        @Schema(description = "사용자 ID", example = "1")
        Long id,

        @Schema(description = "닉네임", example = "진수")
        String nickname,

        @Schema(description = "이메일", example = "jsshin@test.com")
        String email
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getNickname(), user.getEmail());
    }
}
