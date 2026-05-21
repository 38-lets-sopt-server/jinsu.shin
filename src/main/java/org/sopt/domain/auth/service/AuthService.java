package org.sopt.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.sopt.domain.auth.dto.response.TokenResponse;
import org.sopt.domain.auth.entity.RefreshToken;
import org.sopt.domain.auth.repository.RefreshTokenRepository;
import org.sopt.domain.user.dto.response.UserResponse;
import org.sopt.domain.user.entity.User;
import org.sopt.domain.user.repository.UserRepository;
import org.sopt.global.exception.BusinessException;
import org.sopt.global.exception.ErrorCode;
import org.sopt.global.security.JwtService;
import org.sopt.global.security.TokenBlacklistService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklistService tokenBlacklistService;

    @Transactional
    public TokenResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.ATH_401_001));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(ErrorCode.ATH_401_001);
        }

        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        refreshTokenRepository.deleteByUserId(user.getId());
        refreshTokenRepository.flush();
        refreshTokenRepository.save(
                RefreshToken.of(user.getId(), refreshToken, jwtService.getRefreshTokenExpiresInSeconds())
        );

        return TokenResponse.of(accessToken, refreshToken);
    }

    @Transactional
    public TokenResponse reissue(String refreshToken) {
        Long userId = jwtService.verifyAndGetUserId(refreshToken);

        RefreshToken stored = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.ATH_401_002));

        if (stored.isExpired()) {
            refreshTokenRepository.delete(stored);
            throw new BusinessException(ErrorCode.ATH_401_002);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ATH_401_002));

        String newAccessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
        String newRefreshToken = jwtService.generateRefreshToken(user.getId());
        stored.rotate(newRefreshToken, jwtService.getRefreshTokenExpiresInSeconds());

        return TokenResponse.of(newAccessToken, newRefreshToken);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USR_404_001));
        return UserResponse.from(user);
    }

    @Transactional
    public void logout(Long userId, String accessToken) {
        refreshTokenRepository.deleteByUserId(userId);

        String jti = jwtService.getJti(accessToken);
        long remaining = jwtService.getRemainingSeconds(accessToken);
        tokenBlacklistService.add(jti, remaining);
    }
}
