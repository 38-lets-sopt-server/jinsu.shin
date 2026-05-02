package org.sopt.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreatePostResponse(
        @Schema(description = "생성된 게시글 ID", example = "1")
        Long id
) {
}
