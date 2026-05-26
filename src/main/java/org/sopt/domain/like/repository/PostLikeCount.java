package org.sopt.domain.like.repository;

/**
 * 게시글별 좋아요 수 집계 결과. countByPostIds 의 타입 안전한 프로젝션.
 */
public record PostLikeCount(Long postId, Long likeCount) {
}
