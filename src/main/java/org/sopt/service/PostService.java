package org.sopt.service;

import org.sopt.domain.BoardType;
import org.sopt.domain.Post;
import org.sopt.domain.User;
import org.sopt.dto.request.CreatePostRequest;
import org.sopt.dto.request.UpdatePostRequest;
import org.sopt.dto.response.CreatePostResponse;
import org.sopt.dto.response.PostDetailResponse;
import org.sopt.dto.response.PostSearchResponse;
import org.sopt.dto.response.PostSummaryResponse;
import org.sopt.exception.ErrorCode;
import org.sopt.exception.NotFoundException;
import org.sopt.repository.LikeRepository;
import org.sopt.repository.PostRepository;
import org.sopt.repository.UserRepository;
import org.sopt.validator.PostValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository, LikeRepository likeRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
    }

    // CREATE
    @Transactional
    public CreatePostResponse createPost(CreatePostRequest request) {
        PostValidator.validatePost(request.title());
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_001));
        boolean anonymous = request.isAnonymous() != null ? request.isAnonymous() : true;
        Post post = new Post(request.title(), request.content(), user, anonymous, request.boardType());
        postRepository.save(post);
        return new CreatePostResponse(post.getId());
    }

    // READ - 전체
    @Transactional(readOnly = true)
    public List<PostSummaryResponse> getAllPosts(BoardType boardType, int page, int size) {
        List<Post> posts = (boardType != null)
                ? postRepository.findByBoardTypeWithUser(boardType)
                : postRepository.findAllWithUser();

        Map<Long, Long> likeCountMap = likeRepository.countGroupByPostId()
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        int from = page * size;
        if (from >= posts.size()) {
            return List.of();
        }
        int to = Math.min(from + size, posts.size());
        return posts.subList(from, to).stream()
                .map(post -> PostSummaryResponse.from(post, likeCountMap.getOrDefault(post.getId(), 0L)))
                .toList();
    }

    // SEARCH
    @Transactional(readOnly = true)
    public List<PostSearchResponse> searchPosts(String title, String nickname) {
        return postRepository.searchPosts(title, nickname)
                .stream()
                .map(PostSearchResponse::from)
                .toList();
    }

    // READ - 단건
    @Transactional(readOnly = true)
    public PostDetailResponse getPost(Long id) {
        Post post = postRepository.findByIdWithUser(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_001));
        return PostDetailResponse.from(post);
    }

    // UPDATE
    @Transactional
    public PostDetailResponse updatePost(Long id, UpdatePostRequest request) {
        PostValidator.validatePost(request.title());
        Post post = postRepository.findByIdWithUser(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_001));
        post.update(request.title(), request.content());
        return PostDetailResponse.from(post);
    }

    // DELETE
    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_001));
        postRepository.delete(post);
    }
}
