package org.sopt.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.domain.auth.dto.request.LoginRequest;
import org.sopt.domain.auth.dto.response.TokenResponse;
import org.sopt.domain.auth.service.AuthService;
import org.sopt.domain.user.dto.response.UserResponse;
import org.sopt.global.exception.BusinessException;
import org.sopt.global.exception.ErrorCode;
import org.sopt.global.exception.SuccessCode;
import org.sopt.global.response.ApiResponseBody;
import org.sopt.global.security.LoginUserId;
import org.sopt.global.swagger.CustomExceptionDescription;
import org.sopt.global.swagger.SwaggerResponseDescription;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인 (Access Token + Refresh Token 발급)")
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    @CustomExceptionDescription(SwaggerResponseDescription.LOGIN)
    @PostMapping("/login")
    public ResponseEntity<ApiResponseBody<TokenResponse, Void>> login(
            @RequestBody LoginRequest request
    ) {
        TokenResponse tokens = authService.login(request.email(), request.password());
        return ResponseEntity.ok(ApiResponseBody.ok(SuccessCode.OK, tokens));
    }

    @Operation(summary = "토큰 재발급", description = "Refresh Token으로 새 Access/Refresh Token을 발급받습니다.")
    @ApiResponse(responseCode = "200", description = "재발급 성공")
    @CustomExceptionDescription(SwaggerResponseDescription.REISSUE)
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponseBody<TokenResponse, Void>> reissue(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization
    ) {
        String refreshToken = resolveBearerToken(authorization);
        TokenResponse tokens = authService.reissue(refreshToken);
        return ResponseEntity.ok(ApiResponseBody.ok(SuccessCode.OK, tokens));
    }

    @Operation(summary = "로그아웃", description = "Refresh Token 을 DB 에서 삭제하고 현재 Access Token 을 블랙리스트에 등록합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    @CustomExceptionDescription(SwaggerResponseDescription.LOGOUT)
    @PostMapping("/logout")
    public ResponseEntity<ApiResponseBody<Void, Void>> logout(
            @LoginUserId Long userId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        String accessToken = resolveBearerToken(authorization);
        authService.logout(userId, accessToken);
        return ResponseEntity.ok(ApiResponseBody.ok(SuccessCode.OK));
    }

    @Operation(summary = "내 정보 조회 (Access Token 검증)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @CustomExceptionDescription(SwaggerResponseDescription.GET_MY_INFO)
    @GetMapping("/me")
    public ResponseEntity<ApiResponseBody<UserResponse, Void>> me(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        Long userId = Long.parseLong(authentication.getName());
        UserResponse response = authService.getUserById(userId);
        return ResponseEntity.ok(ApiResponseBody.ok(SuccessCode.OK, response));
    }

    private String resolveBearerToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
        return authorization.substring("Bearer ".length()).trim();
    }
}
