package org.sopt.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.sopt.domain.auth.dto.oauth.KakaoUserInfoResponse;
import org.sopt.domain.auth.dto.response.TokenResponse;
import org.sopt.domain.user.entity.Provider;
import org.sopt.domain.user.entity.User;
import org.sopt.domain.user.repository.UserRepository;
import org.sopt.global.exception.BusinessException;
import org.sopt.global.exception.ErrorCode;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final UserRepository userRepository;
    private final TokenIssuer tokenIssuer;
    private final KakaoApiClient kakaoApiClient;

    public TokenResponse loginWithKakao(String code) {
        // 카카오 토큰 교환·유저 조회는 외부 HTTP I/O 이므로 트랜잭션 밖에서 수행한다.
        String kakaoAccessToken = kakaoApiClient.requestAccessToken(code);
        KakaoUserInfoResponse userInfo = kakaoApiClient.requestUserInfo(kakaoAccessToken);

        String providerId = String.valueOf(userInfo.id());
        String email = userInfo.kakaoAccount() != null ? userInfo.kakaoAccount().email() : null;
        String nickname = userInfo.kakaoAccount() != null && userInfo.kakaoAccount().profile() != null
                ? userInfo.kakaoAccount().profile().nickname()
                : null;

        User user = userRepository.findByProviderAndProviderId(Provider.KAKAO, providerId)
                .orElseGet(() -> registerKakaoUser(email, nickname, providerId));

        return tokenIssuer.issue(user);
    }

    private User registerKakaoUser(String email, String nickname, String providerId) {
        if (email != null) {
            userRepository.findByEmail(email).ifPresent(u -> {
                throw new BusinessException(ErrorCode.OAUTH_EMAIL_ALREADY_REGISTERED);
            });
        }
        return userRepository.save(User.oauth(nickname, email, Provider.KAKAO, providerId));
    }
}
