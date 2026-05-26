package org.sopt.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.sopt.domain.auth.dto.oauth.KakaoTokenResponse;
import org.sopt.domain.auth.dto.oauth.KakaoUserInfoResponse;
import org.sopt.global.exception.BusinessException;
import org.sopt.global.exception.ErrorCode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class KakaoApiClient {

    private final RestClient restClient;
    private final KakaoOAuthProperties properties;

    public String requestAccessToken(String code) {
        try {
            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("grant_type", "authorization_code");
            form.add("client_id", properties.clientId());
            form.add("client_secret", properties.clientSecret());
            form.add("redirect_uri", properties.redirectUri());
            form.add("code", code);

            KakaoTokenResponse response = restClient.post()
                    .uri(properties.tokenUri())
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

    public KakaoUserInfoResponse requestUserInfo(String accessToken) {
        try {
            KakaoUserInfoResponse response = restClient.get()
                    .uri(properties.userInfoUri())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
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
