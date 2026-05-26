package org.sopt.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.sopt.domain.auth.dto.oauth.KakaoTokenResponse;
import org.sopt.domain.auth.dto.oauth.KakaoUserInfoResponse;
import org.sopt.domain.auth.dto.response.TokenResponse;
import org.sopt.domain.user.entity.Provider;
import org.sopt.domain.user.entity.User;
import org.sopt.domain.user.repository.UserRepository;
import org.sopt.global.exception.BusinessException;
import org.sopt.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final UserRepository userRepository;
    private final TokenIssuer tokenIssuer;
    private final RestClient restClient;

    @Value("${oauth.kakao.client-id}")
    private String kakaoClientId;

    @Value("${oauth.kakao.client-secret}")
    private String kakaoClientSecret;

    @Value("${oauth.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${oauth.kakao.token-uri}")
    private String kakaoTokenUri;

    @Value("${oauth.kakao.user-info-uri}")
    private String kakaoUserInfoUri;

    public TokenResponse loginWithKakao(String code) {
        // 카카오 토큰 교환·유저 조회는 외부 HTTP I/O 이므로 트랜잭션 밖에서 수행한다.
        // DB 트랜잭션·커넥션을 카카오 응답 대기 동안 잡지 않기 위함.
        String kakaoAccessToken = requestKakaoAccessToken(code);
        KakaoUserInfoResponse userInfo = requestKakaoUserInfo(kakaoAccessToken);

        // 이하 DB 작업: 신규 유저 저장과 토큰 발급(tokenIssuer.issue 자체가 @Transactional)은
        // 각자 짧은 트랜잭션으로 처리된다. 동일 (provider, providerId) 중복 생성은
        // 트랜잭션 격리가 아니라 users 테이블의 유니크 제약으로 막는다.
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

    private String requestKakaoAccessToken(String code) {
        try {
            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("grant_type", "authorization_code");
            form.add("client_id", kakaoClientId);
            form.add("client_secret", kakaoClientSecret);
            form.add("redirect_uri", kakaoRedirectUri);
            form.add("code", code);

            KakaoTokenResponse response = restClient.post()
                    .uri(kakaoTokenUri)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(KakaoTokenResponse.class);

            if (response == null || response.accessToken() == null) {
                throw new BusinessException(ErrorCode.OAUTH_AUTHENTICATION_FAILED);
            }
            return response.accessToken();
        } catch (RestClientException e) {
            throw new BusinessException(ErrorCode.OAUTH_SERVER_ERROR);
        }
    }

    private KakaoUserInfoResponse requestKakaoUserInfo(String kakaoAccessToken) {
        try {
            KakaoUserInfoResponse response = restClient.get()
                    .uri(kakaoUserInfoUri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + kakaoAccessToken)
                    .retrieve()
                    .body(KakaoUserInfoResponse.class);

            if (response == null || response.id() == null) {
                throw new BusinessException(ErrorCode.OAUTH_AUTHENTICATION_FAILED);
            }
            return response;
        } catch (RestClientException e) {
            throw new BusinessException(ErrorCode.OAUTH_SERVER_ERROR);
        }
    }
}
