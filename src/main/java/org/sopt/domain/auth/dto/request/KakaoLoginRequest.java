package org.sopt.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record KakaoLoginRequest(
        @Schema(description = "카카오로부터 받은 authorization code", example = "abcdef123456...")
        @NotBlank
        String code
) {
}
