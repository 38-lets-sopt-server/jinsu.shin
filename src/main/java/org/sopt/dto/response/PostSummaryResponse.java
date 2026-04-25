package org.sopt.dto.response;

import org.sopt.domain.BoardType;
import org.sopt.domain.Post;

public record PostSummaryResponse(
        Long id,
        String title,
        String content,
        String author,
        String createdAt,
        boolean isAnonymous,
        BoardType boardType
) {
    public static PostSummaryResponse from(Post post) {
        String author = post.isAnonymous() ? "익명" : post.getUser().getNickname();
        return new PostSummaryResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                author,
                post.getCreatedAt().toString(),
                post.isAnonymous(),
                post.getBoardType()
        );
    }
}
