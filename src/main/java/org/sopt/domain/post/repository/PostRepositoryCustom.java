package org.sopt.domain.post.repository;

import org.sopt.domain.post.entity.Post;

import java.util.List;

public interface PostRepositoryCustom {
    List<Post> searchPosts(String title, String nickname);
}
