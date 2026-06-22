package org.sopt.domain.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.sopt.domain.post.entity.BoardType;

public record CreatePostRequest(
        @Schema(description = "게시글 제목", example = "오늘 학식 뭐임")
        @NotBlank @Size(max = 50)
        String title,

        @Schema(description = "게시글 본문 (선택)", example = "돈까스래")
        String content,

        @Schema(description = "익명 여부 (생략 시 기본값 true)", example = "true")
        Boolean isAnonymous,

        @Schema(description = "게시판 종류", example = "FREE")
        @NotNull
        BoardType boardType
) {
}
