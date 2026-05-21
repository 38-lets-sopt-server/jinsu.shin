package org.sopt.domain.post.service;

import org.sopt.domain.post.dto.request.CreatePostRequest;
import org.sopt.domain.post.dto.request.UpdatePostRequest;
import org.sopt.domain.post.dto.response.CreatePostResponse;
import org.sopt.domain.post.dto.response.PostDetailResponse;
import org.sopt.domain.post.dto.response.PostSearchResponse;
import org.sopt.domain.post.dto.response.PostSummaryResponse;
import org.sopt.domain.post.entity.BoardType;
import org.sopt.domain.post.entity.Post;
import org.sopt.domain.post.repository.PostRepository;
import org.sopt.domain.user.entity.User;
import org.sopt.domain.user.repository.UserRepository;
import org.sopt.global.exception.BusinessException;
import org.sopt.global.exception.ErrorCode;
import org.sopt.domain.like.repository.LikeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PostService {

    private static final int TITLE_MAX_LENGTH = 50;

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository, LikeRepository likeRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
    }

    @Transactional
    public CreatePostResponse createPost(CreatePostRequest request) {
        validateTitle(request.title());
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USR_404_001));
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

    @Transactional(readOnly = true)
    public List<PostSearchResponse> searchPosts(String title, String nickname) {
        return postRepository.searchPosts(title, nickname)
                .stream()
                .map(PostSearchResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getPost(Long id) {
        Post post = postRepository.findByIdWithUser(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.POS_404_001));
        return PostDetailResponse.from(post);
    }

    @Transactional
    public PostDetailResponse updatePost(Long id, UpdatePostRequest request) {
        validateTitle(request.title());
        Post post = postRepository.findByIdWithUser(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.POS_404_001));
        post.update(request.title(), request.content());
        return PostDetailResponse.from(post);
    }

    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.POS_404_001));
        postRepository.delete(post);
    }

    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new BusinessException(ErrorCode.POS_400_001);
        }
        if (title.length() > TITLE_MAX_LENGTH) {
            throw new BusinessException(ErrorCode.POS_400_002);
        }
    }
}
