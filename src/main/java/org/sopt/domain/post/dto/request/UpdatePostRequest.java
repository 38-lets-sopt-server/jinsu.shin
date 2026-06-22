package org.sopt.domain.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePostRequest(
        @Schema(description = "수정할 제목", example = "수정된 제목")
        @NotBlank @Size(max = 50)
        String title,

        @Schema(description = "수정할 본문 (선택)", example = "수정된 내용")
        String content
) {
}
