package org.sopt.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.domain.Post;

public record PostSearchResponse(
        @Schema(description = "게시글 ID", example = "1")
        Long id,

        @Schema(description = "게시글 제목", example = "오늘 학식 뭐임")
        String title,

        @Schema(description = "게시글 본문", example = "돈까스래")
        String content,

        @Schema(description = "작성자 닉네임 (익명이면 '익명')", example = "익명")
        String authorNickname,

        @Schema(description = "작성 일시", example = "2024-03-27T15:22:00")
        String createdAt
) {
    public static PostSearchResponse from(Post post) {
        String authorNickname = post.isAnonymous() ? "익명" : post.getUser().getNickname();
        return new PostSearchResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                authorNickname,
                post.getCreatedAt().toString()
        );
    }
}
