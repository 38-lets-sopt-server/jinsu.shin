package org.sopt.repository;

import org.sopt.domain.BoardType;
import org.sopt.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    @Query("SELECT p FROM Post p JOIN FETCH p.user")
    List<Post> findAllWithUser();

    @Query("SELECT p FROM Post p JOIN FETCH p.user WHERE p.boardType = :boardType")
    List<Post> findByBoardTypeWithUser(@Param("boardType") BoardType boardType);

}
