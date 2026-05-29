package org.sopt.domain.post.repository;

import org.sopt.domain.post.entity.BoardType;
import org.sopt.domain.post.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    @Query("SELECT p FROM Post p JOIN FETCH p.user WHERE p.id = :id")
    Optional<Post> findByIdWithUser(@Param("id") Long id);

    // 커서 기반 무한 스크롤 피드. cursor(마지막으로 본 id)보다 과거 글을 id 내림차순으로 조회한다.
    // cursor 가 null 이면 최신부터, boardType 이 null 이면 전체 게시판.
    // user 는 @EntityGraph 로 fetch(ToOne 이라 limit 과 함께 DB 레벨 처리). hasNext 판정용 size+1 은
    // 호출 측이 Pageable 로 지정한다(반환 타입이 List 이므로 COUNT 쿼리는 실행되지 않음).
    @EntityGraph(attributePaths = "user")
    @Query("SELECT p FROM Post p "
            + "WHERE (:cursor IS NULL OR p.id < :cursor) "
            + "AND (:boardType IS NULL OR p.boardType = :boardType) "
            + "ORDER BY p.id DESC")
    List<Post> findFeed(@Param("cursor") Long cursor,
                        @Param("boardType") BoardType boardType,
                        Pageable pageable);
}