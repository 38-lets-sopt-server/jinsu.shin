package org.sopt.controller;

import org.sopt.domain.Post;
import org.sopt.dto.request.CreatePostRequest;
import org.sopt.dto.response.CommonResponse;
import org.sopt.exception.PostNotFoundException;
import org.sopt.service.PostService;

import java.util.List;

public class PostController {
    private final PostService postService = new PostService();

    // POST /posts
    public CommonResponse<Long> createPost(CreatePostRequest request) {
        try {
            return postService.createPost(request);
        } catch (IllegalArgumentException e) {
            return CommonResponse.error("🚫 " + e.getMessage());
        }
    }

    // GET /posts
    public CommonResponse<List<Post>> getAllPosts() {
        return postService.getAllPosts();
    }

    // GET /posts/{id}
    public CommonResponse<Post> getPost(Long id) {
        try {
            return postService.getPost(id);
        } catch (PostNotFoundException e) {
            return CommonResponse.error("🚫 " + e.getMessage());
        }
    }

    // PUT /posts/{id}
    public CommonResponse<Void> updatePost(Long id, String newTitle, String newContent) {
        try {
            return postService.updatePost(id, newTitle, newContent);
        } catch (PostNotFoundException e) {
            return CommonResponse.error("🚫 " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return CommonResponse.error("🚫 " + e.getMessage());
        }
    }

    // DELETE /posts/{id}
    public CommonResponse<Void> deletePost(Long id) {
        try {
            return postService.deletePost(id);
        } catch (PostNotFoundException e) {
            return CommonResponse.error("🚫 " + e.getMessage());
        }
    }
}
