package org.sopt.dto.response;

import org.sopt.domain.BoardType;
import org.sopt.domain.Post;

public record PostDetailResponse(
        Long id,
        String title,
        String content,
        String author,
        String createdAt,
        boolean isAnonymous,
        BoardType boardType
) {
    public static PostDetailResponse from(Post post) {
        String author = post.isAnonymous() ? "익명" : post.getUser().getNickname();
        return new PostDetailResponse(
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
