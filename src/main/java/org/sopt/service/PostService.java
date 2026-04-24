package org.sopt.service;

import org.sopt.domain.BoardType;
import org.sopt.domain.Post;
import org.sopt.dto.request.CreatePostRequest;
import org.sopt.dto.request.UpdatePostRequest;
import org.sopt.dto.response.PostResponse;
import org.sopt.exception.PostNotFoundException;
import org.sopt.repository.PostRepository;
import org.sopt.validator.PostValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // CREATE
    public Long createPost(CreatePostRequest request) {
        PostValidator.validatePost(request.title());
        String createdAt = java.time.LocalDateTime.now().toString();
        Post post = new Post(postRepository.generateId(), request.title(), request.content(), request.author(), createdAt, request.isAnonymous(), request.boardType());
        postRepository.save(post);
        return post.getId();
    }

    // READ - 전체
    public List<PostResponse> getAllPosts(BoardType boardType) {
        List<Post> posts = (boardType != null)
                ? postRepository.findByBoardType(boardType)
                : postRepository.findAll();
        return posts.stream()
                .map(PostResponse::from)
                .toList();
    }

    // READ - 단건
    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
        return PostResponse.from(post);
    }

    // UPDATE
    public void updatePost(Long id, UpdatePostRequest request) {
        PostValidator.validatePost(request.title());
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
        post.update(request.title(), request.content());
    }

    // DELETE
    public void deletePost(Long id) {
        if (!postRepository.deleteById(id)) {
            throw new PostNotFoundException(id);
        }
    }
}
