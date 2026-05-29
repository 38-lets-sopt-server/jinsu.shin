package org.sopt.domain.post.repository;

import org.sopt.domain.post.entity.Post;

import java.util.List;

public interface PostRepositoryCustom {
    List<Post> searchFeed(String title, String nickname, Long cursor, int limit);
}