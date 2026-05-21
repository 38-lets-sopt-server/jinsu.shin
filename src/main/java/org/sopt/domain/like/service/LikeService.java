package org.sopt.domain.like.service;

import lombok.RequiredArgsConstructor;
import org.sopt.domain.like.entity.Like;
import org.sopt.domain.like.repository.LikeRepository;
import org.sopt.domain.post.entity.Post;
import org.sopt.domain.post.repository.PostRepository;
import org.sopt.domain.user.entity.User;
import org.sopt.domain.user.repository.UserRepository;
import org.sopt.global.exception.BusinessException;
import org.sopt.global.exception.ErrorCode;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addLike(Long postId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USR_404_001));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POS_404_001));

        try {
            if (likeRepository.findByUserAndPost(user, post).isPresent()) {
                throw new BusinessException(ErrorCode.LIK_409_001);
            }
            likeRepository.save(new Like(user, post));
        } catch (ObjectOptimisticLockingFailureException e) {
            if (likeRepository.findByUserAndPost(user, post).isPresent()) {
                throw new BusinessException(ErrorCode.LIK_409_001);
            }
            likeRepository.save(new Like(user, post));
        }
    }

    @Transactional
    public void cancelLike(Long postId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USR_404_001));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POS_404_001));

        Like like = likeRepository.findByUserAndPost(user, post)
                .orElseThrow(() -> new BusinessException(ErrorCode.LIK_404_001));

        likeRepository.delete(like);
    }
}
