package org.sopt.domain.like.repository;

import org.sopt.domain.like.entity.Like;
import org.sopt.domain.post.entity.Post;
import org.sopt.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndPost(User user, Post post);

    // 현재 페이지의 게시글들만 집계한다. 전체 테이블 GROUP BY 를 피하고, Object[] 대신 타입 프로젝션을 사용.
    @Query("SELECT new org.sopt.domain.like.repository.PostLikeCount(l.post.id, COUNT(l)) "
            + "FROM PostLike l WHERE l.post.id IN :postIds GROUP BY l.post.id")
    List<PostLikeCount> countByPostIds(@Param("postIds") List<Long> postIds);
}