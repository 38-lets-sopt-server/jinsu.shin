package org.sopt.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.sopt.domain.user.dto.request.UserCreateRequest;
import org.sopt.domain.user.dto.response.UserResponse;
import org.sopt.domain.user.entity.User;
import org.sopt.domain.user.repository.UserRepository;
import org.sopt.global.exception.BusinessException;
import org.sopt.global.exception.ErrorCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse join(UserCreateRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(u -> {
            throw new BusinessException(ErrorCode.USR_409_001);
        });
        String encodedPassword = passwordEncoder.encode(request.password());
        User user = userRepository.save(new User(request.nickname(), request.email(), encodedPassword));
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USR_404_001));
        return UserResponse.from(user);
    }
}
