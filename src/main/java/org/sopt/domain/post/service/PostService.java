package org.sopt.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.sopt.domain.like.repository.LikeRepository;
import org.sopt.domain.post.dto.request.CreatePostRequest;
import org.sopt.domain.post.dto.request.UpdatePostRequest;
import org.sopt.domain.post.dto.response.CreatePostResponse;
import org.sopt.domain.post.dto.response.PostDetailResponse;
import org.sopt.domain.post.dto.response.PostSummaryResponse;
import org.sopt.domain.post.entity.BoardType;
import org.sopt.domain.post.entity.Post;
import org.sopt.domain.post.repository.PostRepository;
import org.sopt.domain.user.entity.User;
import org.sopt.domain.user.repository.UserRepository;
import org.sopt.global.exception.BusinessException;
import org.sopt.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private static final int TITLE_MAX_LENGTH = 50;

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    @Transactional
    public CreatePostResponse createPost(Long userId, CreatePostRequest request) {
        validateTitle(request.title());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        boolean anonymous = request.isAnonymous() != null ? request.isAnonymous() : true;
        Post post = new Post(request.title(), request.content(), user, anonymous, request.boardType());
        postRepository.save(post);
        return new CreatePostResponse(post.getId());
    }

    @Transactional(readOnly = true)
    public List<PostSummaryResponse> getAllPosts(BoardType boardType, int page, int size) {
        List<Post> posts = (boardType != null)
                ? postRepository.findByBoardTypeWithUser(boardType)
                : postRepository.findAllWithUser();

        int from = page * size;
        if (from >= posts.size()) {
            return List.of();
        }
        int to = Math.min(from + size, posts.size());
        return toSummaries(posts.subList(from, to));
    }

    @Transactional(readOnly = true)
    public List<PostSummaryResponse> searchPosts(String title, String nickname) {
        return toSummaries(postRepository.searchPosts(title, nickname));
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getPost(Long id) {
        Post post = postRepository.findByIdWithUser(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        return PostDetailResponse.from(post);
    }

    @Transactional
    public PostDetailResponse updatePost(Long userId, Long postId, UpdatePostRequest request) {
        validateTitle(request.title());
        Post post = postRepository.findByIdWithUser(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        validateOwner(post, userId);
        post.update(request.title(), request.content());
        return PostDetailResponse.from(post);
    }

    @Transactional
    public void deletePost(Long userId, Long postId) {
        Post post = postRepository.findByIdWithUser(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        validateOwner(post, userId);
        postRepository.delete(post);
    }

    private List<PostSummaryResponse> toSummaries(List<Post> posts) {
        Map<Long, Long> likeCountMap = likeRepository.countGroupByPostId()
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));
        return posts.stream()
                .map(post -> PostSummaryResponse.from(post, likeCountMap.getOrDefault(post.getId(), 0L)))
                .toList();
    }

    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new BusinessException(ErrorCode.POST_TITLE_REQUIRED);
        }
        if (title.length() > TITLE_MAX_LENGTH) {
            throw new BusinessException(ErrorCode.POST_TITLE_TOO_LONG);
        }
    }

    private static void validateOwner(Post post, Long userId) {
        if (!post.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }
}
