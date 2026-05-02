package org.sopt.repository;

import org.sopt.domain.Post;

import java.util.List;

public interface PostRepositoryCustom {
    List<Post> searchPosts(String title, String nickname);
}
