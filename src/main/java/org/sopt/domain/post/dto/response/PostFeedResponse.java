package org.sopt.domain.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "게시글 피드 (커서 기반 무한 스크롤)")
public record PostFeedResponse(
        @Schema(description = "게시글 목록")
        List<PostSummaryResponse> items,

        @Schema(description = "다음 요청에 보낼 cursor (마지막 게시글 id). 더 없으면 null", example = "27")
        Long nextCursor,

        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext
) {
    public static PostFeedResponse of(List<PostSummaryResponse> items, Long nextCursor, boolean hasNext) {
        return new PostFeedResponse(items, nextCursor, hasNext);
    }
}
