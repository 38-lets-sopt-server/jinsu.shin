package org.sopt.service;

import org.sopt.domain.User;
import org.sopt.dto.request.UserCreateRequest;
import org.sopt.dto.response.UserResponse;
import org.sopt.exception.ConflictException;
import org.sopt.global.exception.ErrorCode;
import org.sopt.exception.NotFoundException;
import org.sopt.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponse join(UserCreateRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(u -> {
            throw new ConflictException(ErrorCode.USR_409_001);
        });
        User user = userRepository.save(new User(request.nickname(), request.email(), request.password()));
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USR_404_001));
        return UserResponse.from(user);
    }
}
