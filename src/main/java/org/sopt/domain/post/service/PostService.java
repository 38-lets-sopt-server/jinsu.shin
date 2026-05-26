package org.sopt.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.sopt.domain.like.repository.LikeRepository;
import org.sopt.domain.like.repository.PostLikeCount;
import org.sopt.domain.post.dto.request.CreatePostRequest;
import org.sopt.domain.post.dto.request.UpdatePostRequest;
import org.sopt.domain.post.dto.response.CreatePostResponse;
import org.sopt.domain.post.dto.response.PostDetailResponse;
import org.sopt.domain.post.dto.response.PostFeedResponse;
import org.sopt.domain.post.dto.response.PostSummaryResponse;
import org.sopt.domain.post.entity.BoardType;
import org.sopt.domain.post.entity.Post;
import org.sopt.domain.post.repository.PostRepository;
import org.sopt.domain.user.entity.User;
import org.sopt.domain.user.repository.UserRepository;
import org.sopt.global.exception.BusinessException;
import org.sopt.global.exception.ErrorCode;
import org.springframework.data.domain.PageRequest;
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
    public PostFeedResponse getFeed(BoardType boardType, Long cursor, int size) {
        // hasNext 판정을 위해 size + 1 개를 가져온다(추가 COUNT 쿼리 없이 다음 페이지 유무 확인).
        List<Post> fetched = postRepository.findFeed(cursor, boardType, PageRequest.of(0, size + 1));
        return toFeed(fetched, size);
    }

    @Transactional(readOnly = true)
    public PostFeedResponse searchFeed(String title, String nickname, Long cursor, int size) {
        List<Post> fetched = postRepository.searchFeed(title, nickname, cursor, size + 1);
        return toFeed(fetched, size);
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

    // size + 1 개 조회 결과로 hasNext 와 nextCursor 를 계산하고, 응답 items 는 size 개로 자른다.
    private PostFeedResponse toFeed(List<Post> fetched, int size) {
        boolean hasNext = fetched.size() > size;
        List<Post> page = hasNext ? fetched.subList(0, size) : fetched;
        List<PostSummaryResponse> items = toSummaries(page);
        Long nextCursor = hasNext ? page.get(page.size() - 1).getId() : null;
        return PostFeedResponse.of(items, nextCursor, hasNext);
    }

    private List<PostSummaryResponse> toSummaries(List<Post> posts) {
        if (posts.isEmpty()) {
            return List.of();
        }
        List<Long> postIds = posts.stream().map(Post::getId).toList();
        Map<Long, Long> likeCountMap = likeRepository.countByPostIds(postIds).stream()
                .collect(Collectors.toMap(PostLikeCount::postId, PostLikeCount::likeCount));
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
