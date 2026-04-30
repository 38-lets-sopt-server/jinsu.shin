package org.sopt.dto.request;

import org.sopt.domain.BoardType;

public record CreatePostRequest(
        Long userId,
        String title,
        String content,
        Boolean isAnonymous,
        BoardType boardType
) {
}
