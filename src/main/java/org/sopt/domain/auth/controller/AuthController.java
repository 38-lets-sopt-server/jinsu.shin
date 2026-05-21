package org.sopt.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.domain.auth.dto.response.TokenResponse;
import org.sopt.domain.auth.service.AuthService;
import org.sopt.domain.user.dto.response.UserResponse;
import org.sopt.global.exception.BusinessException;
import org.sopt.global.exception.ErrorCode;
import org.sopt.global.exception.SuccessCode;
import org.sopt.global.response.ApiResponseBody;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인 (Access Token + Refresh Token 발급)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "이메일/비밀번호 불일치")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponseBody<TokenResponse, Void>> login(
            @RequestParam("email") String email,
            @RequestParam("password") String password
    ) {
        TokenResponse tokens = authService.login(email, password);
        return ResponseEntity.ok(ApiResponseBody.ok(SuccessCode.OK, tokens));
    }

    @Operation(summary = "토큰 재발급", description = "Refresh Token으로 새 Access/Refresh Token을 발급받습니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "재발급 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Refresh Token이 유효하지 않거나 만료됨")
    })
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponseBody<TokenResponse, Void>> reissue(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization
    ) {
        String refreshToken = resolveBearerToken(authorization);
        TokenResponse tokens = authService.reissue(refreshToken);
        return ResponseEntity.ok(ApiResponseBody.ok(SuccessCode.OK, tokens));
    }

    @Operation(summary = "내 정보 조회 (Access Token 검증)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않음")
    })
    @GetMapping("/me")
    public ResponseEntity<ApiResponseBody<UserResponse, Void>> me(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new BusinessException(ErrorCode.ATH_401_003);
        }
        Long userId = Long.parseLong(authentication.getName());
        UserResponse response = authService.getUserById(userId);
        return ResponseEntity.ok(ApiResponseBody.ok(SuccessCode.OK, response));
    }

    private String resolveBearerToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.ATH_401_002);
        }
        return authorization.substring("Bearer ".length()).trim();
    }
}
