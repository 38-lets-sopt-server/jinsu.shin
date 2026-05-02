package org.sopt.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record LikeRequest(
        @Schema(description = "좋아요를 누르는 사용자 ID", example = "1")
        Long userId
) {
}
