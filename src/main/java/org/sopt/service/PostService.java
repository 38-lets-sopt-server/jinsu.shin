package org.sopt.service;

import org.sopt.domain.BoardType;
import org.sopt.domain.Post;
import org.sopt.dto.request.CreatePostRequest;
import org.sopt.dto.request.UpdatePostRequest;
import org.sopt.dto.response.CreatePostResponse;
import org.sopt.dto.response.PostDetailResponse;
import org.sopt.dto.response.PostSummaryResponse;
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
    public CreatePostResponse createPost(CreatePostRequest request) {
        PostValidator.validatePost(request.title());
        String createdAt = java.time.LocalDateTime.now().toString();
        Post post = new Post(request.title(), request.content(), request.author(), createdAt, request.isAnonymous(), request.boardType());
        postRepository.save(post);
        return new CreatePostResponse(post.getId());
    }

    // READ - 전체
    public List<PostSummaryResponse> getAllPosts(BoardType boardType, int page, int size) {
        List<Post> posts = (boardType != null)
                ? postRepository.findByBoardType(boardType)
                : postRepository.findAll();
        int from = page * size;
        if (from >= posts.size()) {
            return List.of();
        }
        int to = Math.min(from + size, posts.size());
        return posts.subList(from, to).stream()
                .map(PostSummaryResponse::from)
                .toList();
    }

    // READ - 단건
    public PostDetailResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
        return PostDetailResponse.from(post);
    }

    // UPDATE
    public void updatePost(Long id, UpdatePostRequest request) {
        PostValidator.validatePost(request.title());
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
        post.update(request.title(), request.content());
    }

    // DELETE 📝 과제
    public void deletePost(Long id) {
        // TODO
    }
}
