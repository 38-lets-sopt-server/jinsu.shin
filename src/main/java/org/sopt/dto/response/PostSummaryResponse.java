package org.sopt.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.domain.BoardType;
import org.sopt.domain.Post;

public record PostSummaryResponse(
        @Schema(description = "게시글 ID", example = "1")
        Long id,

        @Schema(description = "게시글 제목", example = "오늘 학식 뭐임")
        String title,

        @Schema(description = "게시글 본문", example = "돈까스래")
        String content,

        @Schema(description = "작성자 (익명이면 '익명')", example = "익명")
        String author,

        @Schema(description = "작성 일시", example = "2024-03-27T15:22:00")
        String createdAt,

        @Schema(description = "익명 여부", example = "true")
        boolean isAnonymous,

        @Schema(description = "게시판 종류", example = "FREE")
        BoardType boardType,

        @Schema(description = "좋아요 수", example = "5")
        long likeCount
) {
    public static PostSummaryResponse from(Post post, long likeCount) {
        String author = post.isAnonymous() ? "익명" : post.getUser().getNickname();
        return new PostSummaryResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                author,
                post.getCreatedAt().toString(),
                post.isAnonymous(),
                post.getBoardType(),
                likeCount
        );
    }
}
