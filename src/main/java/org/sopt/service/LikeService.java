package org.sopt.service;

import org.sopt.domain.Like;
import org.sopt.domain.Post;
import org.sopt.domain.User;
import org.sopt.dto.request.LikeRequest;
import org.sopt.exception.ConflictException;
import org.sopt.exception.ErrorCode;
import org.sopt.exception.NotFoundException;
import org.sopt.repository.LikeRepository;
import org.sopt.repository.PostRepository;
import org.sopt.repository.UserRepository;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public LikeService(LikeRepository likeRepository, PostRepository postRepository, UserRepository userRepository) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void addLike(Long postId, LikeRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_001));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_001));

        try {
            if (likeRepository.findByUserAndPost(user, post).isPresent()) {
                throw new ConflictException(ErrorCode.LIKE_001);
            }
            likeRepository.save(new Like(user, post));
        } catch (ObjectOptimisticLockingFailureException e) {
            // 낙관적 락 충돌 시 재시도 — 재시도 시 중복 감지 후 LIKE_001 반환
            if (likeRepository.findByUserAndPost(user, post).isPresent()) {
                throw new ConflictException(ErrorCode.LIKE_001);
            }
            likeRepository.save(new Like(user, post));
        }
    }

    @Transactional
    public void cancelLike(Long postId, LikeRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_001));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_001));

        Like like = likeRepository.findByUserAndPost(user, post)
                .orElseThrow(() -> new NotFoundException(ErrorCode.LIKE_002));

        likeRepository.delete(like);
    }
}
