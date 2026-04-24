package org.sopt.repository;

import org.sopt.domain.BoardType;
import org.sopt.domain.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Post save(Post post);
    Long generateId();
    List<Post> findAll();
    List<Post> findByBoardType(BoardType boardType);
    Optional<Post> findById(Long id);
    boolean deleteById(Long id);
}
