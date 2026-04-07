package org.sopt.service;

import org.sopt.domain.Post;
import org.sopt.dto.request.CreatePostRequest;
import org.sopt.dto.response.CommonResponse;
import org.sopt.repository.PostRepository;
import org.sopt.validator.PostValidator;

import java.util.List;

public class PostService {
    private final PostRepository postRepository = new PostRepository();

    // CREATE
    public CommonResponse<Long> createPost(CreatePostRequest request) {
        PostValidator.validatePost(request.title, request.content);
        String createdAt = java.time.LocalDateTime.now().toString();
        Post post = new Post(postRepository.generateId(), request.title, request.content, request.author, createdAt);
        postRepository.save(post);
        return CommonResponse.success("게시글 등록 완료!", post.getId());
    }

    // READ - 전체
    public CommonResponse<List<Post>> getAllPosts() {
        return CommonResponse.success("게시글 목록 조회 성공", postRepository.findAll());
    }

    // READ - 단건
    public CommonResponse<Post> getPost(Long id) {
        Post post = postRepository.findById(id);
        return CommonResponse.success("게시글 조회 성공", post);
    }

    // UPDATE
    public CommonResponse<Void> updatePost(Long id, String newTitle, String newContent) {
        PostValidator.validatePost(newTitle, newContent);
        Post post = postRepository.findById(id);
        post.update(newTitle, newContent);
        return CommonResponse.success("게시글 수정 완료!");
    }

    // DELETE
    public CommonResponse<Void> deletePost(Long id) {
        postRepository.deleteById(id);
        return CommonResponse.success("게시글 삭제 완료!");
    }
}
