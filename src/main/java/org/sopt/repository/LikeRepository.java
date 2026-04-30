package org.sopt.repository;

import org.sopt.domain.Like;
import org.sopt.domain.Post;
import org.sopt.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndPost(User user, Post post);

    @Query("SELECT l.post.id, COUNT(l) FROM PostLike l GROUP BY l.post.id")
    List<Object[]> countGroupByPostId();
}
