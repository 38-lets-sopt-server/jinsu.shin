package org.sopt.service;

import org.sopt.domain.BoardType;
import org.sopt.domain.Post;
import org.sopt.domain.User;
import org.sopt.dto.request.CreatePostRequest;
import org.sopt.dto.request.UpdatePostRequest;
import org.sopt.dto.response.CreatePostResponse;
import org.sopt.dto.response.PostDetailResponse;
import org.sopt.dto.response.PostSummaryResponse;
import org.sopt.exception.ErrorCode;
import org.sopt.exception.NotFoundException;
import org.sopt.repository.PostRepository;
import org.sopt.repository.UserRepository;
import org.sopt.validator.PostValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    // CREATE
    @Transactional
    public CreatePostResponse createPost(CreatePostRequest request) {
        PostValidator.validatePost(request.title());
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_001));
        Post post = new Post(request.title(), request.content(), user, LocalDateTime.now(), request.isAnonymous(), request.boardType());
        postRepository.save(post);
        return new CreatePostResponse(post.getId());
    }

    // READ - 전체
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public PostDetailResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_001));
        return PostDetailResponse.from(post);
    }

    // UPDATE
    @Transactional
    public void updatePost(Long id, UpdatePostRequest request) {
        PostValidator.validatePost(request.title());
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_001));
        post.update(request.title(), request.content());
    }

    // DELETE 📝 과제
    public void deletePost(Long id) {
        // TODO
    }
}
