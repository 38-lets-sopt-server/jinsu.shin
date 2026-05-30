package org.sopt.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.domain.auth.dto.request.KakaoLoginRequest;
import org.sopt.domain.auth.dto.response.TokenResponse;
import org.sopt.domain.auth.service.OAuthService;
import org.sopt.global.response.ApiResponseBody;
import org.sopt.global.swagger.CustomExceptionDescription;
import org.sopt.global.swagger.SwaggerResponseDescription;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "OAuth", description = "소셜 로그인 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/oauth")
public class OAuthController {

    private final OAuthService oauthService;

    @Operation(summary = "Kakao 로그인",
            description = "FE 가 전달한 Kakao authorization code 로 우리 서버 JWT(Access/Refresh) 를 발급합니다. 신규 카카오 사용자는 자동 회원가입, 기존 사용자는 로그인 처리합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    @CustomExceptionDescription(SwaggerResponseDescription.KAKAO_LOGIN)
    @PostMapping("/kakao")
    public ResponseEntity<ApiResponseBody<TokenResponse, Void>> loginWithKakao(
            @Valid @RequestBody KakaoLoginRequest request
    ) {
        TokenResponse tokens = oauthService.loginWithKakao(request.code());
        return ResponseEntity.ok(ApiResponseBody.ok(tokens));
    }
}
