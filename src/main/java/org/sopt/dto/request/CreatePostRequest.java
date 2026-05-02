package org.sopt.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.domain.BoardType;

public record CreatePostRequest(
        @Schema(description = "작성자 ID", example = "1")
        Long userId,

        @Schema(description = "게시글 제목 (최대 50자)", example = "오늘 학식 뭐임")
        String title,

        @Schema(description = "게시글 본문 (선택)", example = "돈까스래")
        String content,

        @Schema(description = "익명 여부 (생략 시 기본값 true)", example = "true")
        Boolean isAnonymous,

        @Schema(description = "게시판 종류", example = "FREE")
        BoardType boardType
) {
}
