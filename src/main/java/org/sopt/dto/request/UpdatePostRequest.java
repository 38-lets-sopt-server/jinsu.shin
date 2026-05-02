package org.sopt.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdatePostRequest(
        @Schema(description = "수정할 제목 (최대 50자)", example = "수정된 제목")
        String title,

        @Schema(description = "수정할 본문 (선택)", example = "수정된 내용")
        String content
) {
}
